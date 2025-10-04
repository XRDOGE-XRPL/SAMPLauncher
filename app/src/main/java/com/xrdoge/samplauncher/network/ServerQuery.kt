package com.xrdoge.samplauncher.network

import com.xrdoge.samplauncher.data.models.ServerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer

/**
 * SA-MP server query handler for retrieving server information
 */
class ServerQuery {
    
    companion object {
        private const val TIMEOUT_MS = 3000
        private const val BUFFER_SIZE = 2048
        
        // SA-MP Query opcodes
        private const val OPCODE_INFO = 'i'.code.toByte()
        private const val OPCODE_RULES = 'r'.code.toByte()
        private const val OPCODE_CLIENTS = 'c'.code.toByte()
        private const val OPCODE_DETAILED = 'd'.code.toByte()
    }
    
    /**
     * Query server for basic information
     */
    suspend fun getServerInfo(ip: String, port: Int): ServerInfo? = 
        withContext(Dispatchers.IO) {
            try {
                val socket = DatagramSocket()
                socket.soTimeout = TIMEOUT_MS
                
                val packet = buildQueryPacket(ip, port, OPCODE_INFO)
                socket.send(packet)
                
                val responseData = ByteArray(BUFFER_SIZE)
                val responsePacket = DatagramPacket(responseData, responseData.size)
                socket.receive(responsePacket)
                
                socket.close()
                
                parseServerInfoResponse(responseData, responsePacket.length, ip, port)
            } catch (e: Exception) {
                null
            }
        }
    
    /**
     * Build SA-MP query packet
     */
    private fun buildQueryPacket(ip: String, port: Int, opcode: Byte): DatagramPacket {
        val buffer = ByteBuffer.allocate(11)
        
        // SA-MP query packet structure:
        // 4 bytes: "SAMP"
        // 4 bytes: IP address (split into octets)
        // 2 bytes: Port (little endian)
        // 1 byte: Opcode
        
        buffer.put("SAMP".toByteArray())
        
        // Convert IP string to bytes
        val ipParts = ip.split(".")
        ipParts.forEach { buffer.put(it.toInt().toByte()) }
        
        // Port in little endian
        buffer.put((port and 0xFF).toByte())
        buffer.put((port shr 8 and 0xFF).toByte())
        
        // Opcode
        buffer.put(opcode)
        
        val data = buffer.array()
        val address = InetAddress.getByName(ip)
        
        return DatagramPacket(data, data.size, address, port)
    }
    
    /**
     * Parse server info response packet
     */
    private fun parseServerInfoResponse(
        data: ByteArray, 
        length: Int,
        ip: String,
        port: Int
    ): ServerInfo? {
        if (length < 11) return null
        
        try {
            val buffer = ByteBuffer.wrap(data, 11, length - 11)
            
            // Read password flag
            val isPassworded = buffer.get() == 1.toByte()
            
            // Read player count
            val playersOnline = buffer.short.toInt()
            val maxPlayers = buffer.short.toInt()
            
            // Read hostname length and string
            val hostnameLength = buffer.int
            val hostnameBytes = ByteArray(hostnameLength)
            buffer.get(hostnameBytes)
            val hostname = String(hostnameBytes)
            
            // Read gamemode length and string
            val gamemodeLength = buffer.int
            val gamemodeBytes = ByteArray(gamemodeLength)
            buffer.get(gamemodeBytes)
            val gameMode = String(gamemodeBytes)
            
            // Read language length and string
            val languageLength = buffer.int
            val languageBytes = ByteArray(languageLength)
            buffer.get(languageBytes)
            val language = String(languageBytes)
            
            return ServerInfo(
                hostname = hostname,
                ip = ip,
                port = port,
                playersOnline = playersOnline,
                maxPlayers = maxPlayers,
                gameMode = gameMode,
                language = language,
                isPassworded = isPassworded
            )
        } catch (e: Exception) {
            return null
        }
    }
}
