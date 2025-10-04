package com.xrdoge.samplauncher.data.models

/**
 * Sealed class representing connection result states
 */
sealed class ConnectionResult {
    object Success : ConnectionResult()
    data class Failed(val reason: String) : ConnectionResult()
    object Connecting : ConnectionResult()
}
