package com.xrdoge.samplauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.xrdoge.samplauncher.data.models.ConnectionResult
import com.xrdoge.samplauncher.network.SAMPConnection
import com.xrdoge.samplauncher.network.ServerQuery
import com.xrdoge.samplauncher.utils.GTAPathFinder
import com.xrdoge.samplauncher.utils.PermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main activity for SA-MP Launcher
 */
class MainActivity : AppCompatActivity() {
    
    private var connection: SAMPConnection? = null
    private val serverQuery = ServerQuery()
    
    // UI elements
    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var connectButton: MaterialButton
    private lateinit var queryServerButton: MaterialButton
    private lateinit var settingsButton: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var serverNameText: TextView
    private lateinit var serverIPText: TextView
    private lateinit var playersOnlineText: TextView
    private lateinit var gtaStatusText: TextView
    
    // Default server settings - can be modified
    private val defaultServerIP = "127.0.0.1"
    private val defaultServerPort = 7777
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupUI()
        checkPermissions()
    }
    
    private fun initializeViews() {
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        connectButton = findViewById(R.id.connectButton)
        queryServerButton = findViewById(R.id.queryServerButton)
        settingsButton = findViewById(R.id.settingsButton)
        progressBar = findViewById(R.id.progressBar)
        serverNameText = findViewById(R.id.serverNameText)
        serverIPText = findViewById(R.id.serverIPText)
        playersOnlineText = findViewById(R.id.playersOnlineText)
        gtaStatusText = findViewById(R.id.gtaStatusText)
    }
    
    private fun setupUI() {
        // Set server info
        serverIPText.text = "$defaultServerIP:$defaultServerPort"
        
        // Connect button click listener
        connectButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            
            if (validateInput(username, password)) {
                connectToServer(username, password)
            }
        }
        
        // Query server button click listener
        queryServerButton.setOnClickListener {
            queryServerInfo()
        }
        
        // Settings button click listener
        settingsButton.setOnClickListener {
            showSettingsDialog()
        }
        
        checkGTAInstallation()
    }
    
    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            Toast.makeText(this, "Bitte Benutzername eingeben", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Bitte Passwort eingeben", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    
    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Einstellungen")
            .setMessage("Einstellungen werden in zukünftigen Versionen verfügbar sein.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun checkPermissions() {
        if (!PermissionManager.hasRequiredPermissions(this)) {
            PermissionManager.requestPermissions(this)
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (PermissionManager.onPermissionResult(requestCode, grantResults)) {
            Toast.makeText(this, "Berechtigungen erteilt", Toast.LENGTH_SHORT).show()
            checkGTAInstallation()
        } else {
            showPermissionDeniedDialog()
        }
    }
    
    private fun checkGTAInstallation() {
        val isInstalled = GTAPathFinder.isGTAInstalled(this)
        gtaStatusText.text = if (isInstalled) {
            "GTA:SA gefunden: ${GTAPathFinder.getGTAPath()}"
        } else {
            "GTA:SA nicht gefunden - Bitte installieren Sie GTA: San Andreas"
        }
        
        if (!isInstalled) {
            showGTANotInstalledDialog()
        }
    }
    
    private fun showGTANotInstalledDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("GTA:SA nicht gefunden")
            .setMessage("GTA: San Andreas ist nicht installiert. Bitte installieren Sie das Spiel, um SA-MP zu nutzen.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Berechtigungen erforderlich")
            .setMessage("Die App benötigt Speicherzugriff, um GTA:SA-Dateien zu finden.")
            .setPositiveButton("Erneut versuchen") { _, _ ->
                PermissionManager.requestPermissions(this)
            }
            .setNegativeButton("Abbrechen") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }
    
    /**
     * Connect to SA-MP server
     */
    private fun connectToServer(username: String, password: String) {
        // Show loading
        connectButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                connection = SAMPConnection(defaultServerIP, defaultServerPort)
                
                val result = connection?.connect(username, password)
                
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    connectButton.isEnabled = true
                    
                    when (result) {
                        is ConnectionResult.Success -> {
                            onConnectionSuccess()
                        }
                        is ConnectionResult.Failed -> {
                            onConnectionFailed(result.reason)
                        }
                        else -> {
                            onConnectionFailed("Unbekannter Fehler")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    connectButton.isEnabled = true
                    onConnectionFailed(e.message ?: "Verbindungsfehler")
                }
            }
        }
    }
    
    /**
     * Query server information
     */
    private fun queryServerInfo() {
        queryServerButton.isEnabled = false
        
        lifecycleScope.launch {
            val serverInfo = serverQuery.getServerInfo(defaultServerIP, defaultServerPort)
            
            withContext(Dispatchers.Main) {
                queryServerButton.isEnabled = true
                
                if (serverInfo != null) {
                    // Update UI with server info
                    serverNameText.text = serverInfo.hostname
                    playersOnlineText.text = "Spieler: ${serverInfo.getPlayerCountString()}"
                    
                    Toast.makeText(
                        this@MainActivity,
                        "Server: ${serverInfo.hostname}\nGamemode: ${serverInfo.gameMode}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Server nicht erreichbar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun onConnectionSuccess() {
        Toast.makeText(this, "Erfolgreich verbunden!", Toast.LENGTH_SHORT).show()
        launchGTASA()
    }
    
    private fun onConnectionFailed(reason: String) {
        Toast.makeText(this, "Verbindung fehlgeschlagen: $reason", Toast.LENGTH_LONG).show()
    }
    
    /**
     * Launch GTA:SA app
     */
    private fun launchGTASA() {
        try {
            val intent = packageManager.getLaunchIntentForPackage(GTAPathFinder.getGTAPackage())
            if (intent != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "GTA:SA konnte nicht gestartet werden", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Fehler beim Starten: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        connection?.disconnect()
    }
}
