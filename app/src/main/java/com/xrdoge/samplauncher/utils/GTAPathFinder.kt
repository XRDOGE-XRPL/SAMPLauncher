package com.xrdoge.samplauncher.utils

import android.content.Context
import android.content.pm.PackageManager
import java.io.File

/**
 * Utility object for finding and verifying GTA:SA installation
 */
object GTAPathFinder {
    private const val GTA_PACKAGE = "com.rockstargames.gtasa"
    private const val GTA_PATH = "/sdcard/Android/data/$GTA_PACKAGE/files/"
    
    /**
     * Check if GTA:SA is installed on the device
     * @param context Android context
     * @return true if GTA:SA is installed and files exist
     */
    fun isGTAInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(GTA_PACKAGE, 0)
            File(GTA_PATH).exists()
        } catch (e: PackageManager.NameNotFoundException) {
            false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get the GTA:SA installation path
     * @return Path to GTA:SA files directory
     */
    fun getGTAPath(): String = GTA_PATH
    
    /**
     * Get the GTA:SA package name
     * @return Package name of GTA:SA app
     */
    fun getGTAPackage(): String = GTA_PACKAGE
}
