package com.xrdoge.samplauncher.data.models

/**
 * Data class representing SA-MP server information
 */
data class ServerInfo(
    val hostname: String,
    val ip: String,
    val port: Int,
    val playersOnline: Int,
    val maxPlayers: Int,
    val gameMode: String,
    val language: String,
    val ping: Int = 0,
    val isPassworded: Boolean = false
) {
    /**
     * Get formatted player count string
     */
    fun getPlayerCountString(): String = "$playersOnline/$maxPlayers"
    
    /**
     * Get formatted address string
     */
    fun getAddressString(): String = "$ip:$port"
}
