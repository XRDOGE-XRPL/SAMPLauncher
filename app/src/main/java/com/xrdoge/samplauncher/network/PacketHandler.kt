package com.xrdoge.samplauncher.network

import java.nio.ByteBuffer

/**
 * Handler for SA-MP network packets
 */
object PacketHandler {
    
    /**
     * Read a null-terminated string from ByteBuffer
     */
    fun readString(buffer: ByteBuffer, maxLength: Int = 256): String {
        val bytes = mutableListOf<Byte>()
        var count = 0
        
        while (buffer.hasRemaining() && count < maxLength) {
            val byte = buffer.get()
            if (byte == 0.toByte()) break
            bytes.add(byte)
            count++
        }
        
        return String(bytes.toByteArray())
    }
    
    /**
     * Write a length-prefixed string to ByteBuffer
     */
    fun writeString(buffer: ByteBuffer, str: String) {
        buffer.putInt(str.length)
        buffer.put(str.toByteArray())
    }
    
    /**
     * Read a length-prefixed string from ByteBuffer
     */
    fun readLengthPrefixedString(buffer: ByteBuffer): String {
        val length = buffer.int
        if (length <= 0 || length > 1024) return ""
        
        val bytes = ByteArray(length)
        buffer.get(bytes)
        return String(bytes)
    }
    
    /**
     * Convert IP string to byte array
     */
    fun ipToBytes(ip: String): ByteArray {
        return ip.split(".").map { it.toInt().toByte() }.toByteArray()
    }
    
    /**
     * Convert byte array to IP string
     */
    fun bytesToIp(bytes: ByteArray): String {
        return bytes.joinToString(".") { (it.toInt() and 0xFF).toString() }
    }
}
