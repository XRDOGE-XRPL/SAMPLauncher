package com.xrdoge.samplauncher.network

import com.xrdoge.samplauncher.data.models.ConnectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.ByteBuffer

/**
 * Handler for SA-MP server connections
 */
class SAMPConnection(
    private val serverIP: String,
    private val serverPort: Int
) {
    private var socket: Socket? = null
    private var isConnected = false
    
    companion object {
        private const val TIMEOUT_MS = 5000
        private const val SAMP_MAGIC = "SAMP"
        private const val SAMP_VERSION: Short = 0x4057
    }
    
    /**
     * Connect to SA-MP server with username and password
     */
    suspend fun connect(username: String, password: String): ConnectionResult = 
        withContext(Dispatchers.IO) {
            try {
                socket = Socket(serverIP, serverPort)
                socket?.soTimeout = TIMEOUT_MS
                
                val outputStream = socket?.getOutputStream()
                val inputStream = socket?.getInputStream()
                
                // Build and send SA-MP handshake packet
                val handshakePacket = buildHandshakePacket(username)
                outputStream?.write(handshakePacket)
                
                // Read server response
                val response = ByteArray(1024)
                val bytesRead = inputStream?.read(response) ?: 0
                
                if (bytesRead > 0) {
                    isConnected = true
                    parseServerResponse(response, bytesRead)
                } else {
                    ConnectionResult.Failed("Keine Antwort vom Server")
                }
                
            } catch (e: SocketTimeoutException) {
                ConnectionResult.Failed("Server antwortet nicht")
            } catch (e: IOException) {
                ConnectionResult.Failed("Verbindungsfehler: ${e.message}")
            } catch (e: Exception) {
                ConnectionResult.Failed("Unbekannter Fehler: ${e.message}")
            }
        }
    
    /**
     * Build SA-MP handshake packet
     */
    private fun buildHandshakePacket(username: String): ByteArray {
        val buffer = ByteBuffer.allocate(256)
        buffer.put(SAMP_MAGIC.toByteArray())
        buffer.putShort(SAMP_VERSION)
        buffer.put(username.length.toByte())
        buffer.put(username.toByteArray())
        
        val result = ByteArray(buffer.position())
        buffer.rewind()
        buffer.get(result)
        return result
    }
    
    /**
     * Parse server response to determine connection result
     */
    private fun parseServerResponse(data: ByteArray, length: Int): ConnectionResult {
        if (length < 4) return ConnectionResult.Failed("Ungültige Response")
        
        val responseCode = data[0].toInt()
        return when (responseCode) {
            0x00 -> ConnectionResult.Success
            0x01 -> ConnectionResult.Failed("Falsches Passwort")
            0x02 -> ConnectionResult.Failed("Server voll")
            0x03 -> ConnectionResult.Failed("Gebannt")
            else -> ConnectionResult.Failed("Unbekannter Fehler (Code: $responseCode)")
        }
    }
    
    /**
     * Disconnect from server
     */
    fun disconnect() {
        try {
            socket?.close()
            socket = null
            isConnected = false
        } catch (e: Exception) {
            // Ignore disconnect errors
        }
    }
    
    /**
     * Check if currently connected
     */
    fun isConnected(): Boolean = isConnected && socket?.isConnected == true
}
