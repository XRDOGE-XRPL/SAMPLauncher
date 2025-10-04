# SAMPLauncher

# SA-MP Android Launcher - Komplette Technische Architektur

## 1. TECHNOLOGIE-STACK

### Android Development
- **Sprache:** Kotlin (nicht Java - moderner und sicherer)
- **IDE:** Android Studio (neueste Version)
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

### Netzwerk & Server Communication
- **Socket Library:** OkHttp3 für HTTP/HTTPS
- **SA-MP Protocol:** Custom UDP Socket Implementation
- **JSON Parsing:** Gson oder Kotlinx Serialization

### Datenbank & Persistence
- **Local Storage:** Room Database (für Spielereinstellungen)
- **Shared Preferences:** Für Login-Token und Config
- **Server Database:** MySQL (dein Gamemode-Backend)

### UI Framework
- **Layout:** XML + Material Design 3
- **Navigation:** Jetpack Navigation Component
- **Async Operations:** Kotlin Coroutines

## 2. PROJEKT-STRUKTUR

```
SAMPLauncher/
├── app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/deinname/samplauncher/
│ │ │ │ ├── MainActivity.kt
│ │ │ │ ├── network/
│ │ │ │ │ ├── SAMPConnection.kt
│ │ │ │ │ ├── ServerQuery.kt
│ │ │ │ │ └── PacketHandler.kt
│ │ │ │ ├── ui/
│ │ │ │ │ ├── ServerListFragment.kt
│ │ │ │ │ ├── LoginFragment.kt
│ │ │ │ │ └── GameFragment.kt
│ │ │ │ ├── data/
│ │ │ │ │ ├── models/
│ │ │ │ │ ├── repository/
│ │ │ │ │ └── database/
│ │ │ │ └── utils/
│ │ │ │ ├── GTAPathFinder.kt
│ │ │ │ └── PermissionManager.kt
│ │ │ ├── res/
│ │ │ │ ├── layout/
│ │ │ │ ├── values/
│ │ │ │ └── drawable/
│ │ │ └── AndroidManifest.xml
│ └── build.gradle.kts
└── build.gradle.kts
```

## 3. BENÖTIGTE DEPENDENCIES (build.gradle.kts)

```kotlin
dependencies {
 // Core Android
 implementation("androidx.core:core-ktx:1.12.0")
 implementation("androidx.appcompat:appcompat:1.6.1")
 implementation("com.google.android.material:material:1.11.0")
 
 // Coroutines
 implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
 
 // Network
 implementation("com.squareup.okhttp3:okhttp:4.12.0")
 implementation("com.google.code.gson:gson:2.10.1")
 
 // Room Database
 implementation("androidx.room:room-runtime:2.6.1")
 implementation("androidx.room:room-ktx:2.6.1")
 
 // Navigation
 implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
 implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
 
 // Lifecycle
 implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
 implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
}
```

## 4. ANDROID PERMISSIONS (AndroidManifest.xml)

```xml
<manifest>
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 
 <!-- Android 13+ -->
 <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
 <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
</manifest>
```

## 5. KERN-KOMPONENTEN IMPLEMENTATION

### A) GTA:SA Path Finder (GTAPathFinder.kt)
```kotlin
object GTAPathFinder {
 private const val GTA_PACKAGE = "com.rockstargames.gtasa"
 private const val GTA_PATH = "/sdcard/Android/data/$GTA_PACKAGE/files/"
 
 fun isGTAInstalled(context: Context): Boolean {
 return try {
 context.packageManager.getPackageInfo(GTA_PACKAGE, 0)
 File(GTA_PATH).exists()
 } catch (e: Exception) {
 false
 }
 }
 
 fun getGTAPath(): String = GTA_PATH
}
```

### B) SA-MP Connection (SAMPConnection.kt)
```kotlin
class SAMPConnection(private val serverIP: String, private val serverPort: Int) {
 private val socket = DatagramSocket()
 
 suspend fun connect(): Result<Boolean> = withContext(Dispatchers.IO) {
 try {
 // SA-MP connection handshake
 val packet = createConnectionPacket()
 socket.send(packet)
 
 val response = ByteArray(1024)
 val receivePacket = DatagramPacket(response, response.size)
 socket.receive(receivePacket)
 
 Result.success(true)
 } catch (e: Exception) {
 Result.failure(e)
 }
 }
 
 private fun createConnectionPacket(): DatagramPacket {
 // SA-MP protocol implementation
 // Hier kommt dein SA-MP Packet Protocol
 }
}
```

### C) Server Query (ServerQuery.kt)
```kotlin
class ServerQuery {
 suspend fun getServerInfo(ip: String, port: Int): ServerInfo? {
 // Query SA-MP server für Info (Spieleranzahl, Ping, etc.)
 // Nutzt SA-MP Query Protocol
 }
}
```

## 6. IMPLEMENTATION REIHENFOLGE

### Phase 1: Setup & Grundlagen (1-2 Tage)
1. Android Studio Projekt erstellen
2. Dependencies einbinden
3. Permissions implementieren
4. GTA Path Finder bauen

### Phase 2: UI Grundgerüst (2-3 Tage)
1. MainActivity mit Navigation
2. Server List Screen
3. Login Screen
4. Basic Material Design UI

### Phase 3: Netzwerk Layer (3-5 Tage)
1. SA-MP Protocol Research
2. Socket Connection implementieren
3. Packet Handler bauen
4. Server Query System

### Phase 4: Integration (2-3 Tage)
1. GTA:SA Launch Logic
2. Server Connection Flow
3. Error Handling
4. Testing

### Phase 5: Features (ongoing)
1. Gamepad Support
2. Custom UI für deine 3 Staaten
3. Economy Integration
4. MySQL Backend Connection

## 7. KRITISCHE RESSOURCEN

### SA-MP Protocol Documentation
- SA-MP Wiki: wiki.sa-mp.com
- Packet Structure: GitHub SA-MP Protocol Specs
- Open Source Launcher: github.com/search?q=samp+android

### Android Development
- Official Docs: developer.android.com
- Kotlin Coroutines: kotlinlang.org/docs/coroutines
- Material Design: material.io

### Testing
- SA-MP Server lokal installieren (Windows/Linux)
- Server Logs aktivieren für Debugging
- Wireshark für Packet Analysis

## 8. HÄUFIGE PROBLEME & LÖSUNGEN

### Problem: App crasht beim Connect
**Lösung:** SA-MP Protocol falsch implementiert - check Packet Structure

### Problem: GTA:SA nicht gefunden
**Lösung:** Storage Permissions nicht granted - Runtime Permission Request

### Problem: Server antwortet nicht
**Lösung:** Firewall oder falscher Port - teste mit SA-MP Client erst

## 9. NÄCHSTE SCHRITTE - KONKRET

**JETZT SOFORT:**
1. Android Studio installieren (falls nicht vorhanden)
2. Neues Projekt erstellen: "Empty Activity"
3. Dependencies aus Punkt 3 in build.gradle.kts einfügen
4. Sync Project with Gradle Files

**DANACH:**
5. GTAPathFinder.kt erstellen und testen
6. Permissions in Manifest einfügen
7. Basic UI für Server List bauen

**DANN:**
8. SA-MP Protocol Research starten
9. Socket Connection implementieren
10. Ersten Connection Test machen

---

**WICHTIG:** Bau das Schritt für Schritt! Nicht alles auf einmal. Jede Phase muss funktionieren bevor du weiter gehst.

Fang mit Phase 1 an - GTA Path Detection. Das ist deine Basis! 🎯

## 🔧 Erweiterte Implementation Details

### Server Connection - Detaillierter Code

```kotlin
// SAMPConnection.kt - Vollständige Implementation
class SAMPConnection(private val serverIP: String, private val serverPort: Int) {
 private var socket: Socket? = null
 private var isConnected = false
 
 fun connect(username: String, password: String): ConnectionResult {
 try {
 socket = Socket(serverIP, serverPort)
 socket?.soTimeout = 5000 // 5 Sekunden Timeout
 
 val outputStream = socket?.getOutputStream()
 val inputStream = socket?.getInputStream()
 
 // SA-MP Handshake Packet senden
 val handshakePacket = buildHandshakePacket(username)
 outputStream?.write(handshakePacket)
 
 // Server Response lesen
 val response = ByteArray(1024)
 val bytesRead = inputStream?.read(response) ?: 0
 
 if (bytesRead > 0) {
 return parseServerResponse(response, bytesRead)
 }
 
 return ConnectionResult.Failed("Keine Antwort vom Server")
 
 } catch (e: SocketTimeoutException) {
 return ConnectionResult.Failed("Server antwortet nicht")
 } catch (e: IOException) {
 return ConnectionResult.Failed("Verbindungsfehler: ${e.message}")
 }
 }
 
 private fun buildHandshakePacket(username: String): ByteArray {
 // SA-MP Protocol: 'SAMP' + Version + Username Length + Username
 val buffer = ByteBuffer.allocate(256)
 buffer.put("SAMP".toByteArray())
 buffer.putShort(0x4057) // SA-MP Version
 buffer.put(username.length.toByte())
 buffer.put(username.toByteArray())
 return buffer.array()
 }
 
 private fun parseServerResponse(data: ByteArray, length: Int): ConnectionResult {
 // Response analysieren
 if (length < 4) return ConnectionResult.Failed("Ungültige Response")
 
 val responseCode = data[0].toInt()
 return when (responseCode) {
 0x00 -> ConnectionResult.Success
 0x01 -> ConnectionResult.Failed("Falsches Passwort")
 0x02 -> ConnectionResult.Failed("Server voll")
 0x03 -> ConnectionResult.Failed("Gebannt")
 else -> ConnectionResult.Failed("Unbekannter Fehler")
 }
 }
}

sealed class ConnectionResult {
 object Success : ConnectionResult()
 data class Failed(val reason: String) : ConnectionResult()
}
```

### UI Implementation - Material Design

```kotlin
// MainActivity.kt - Vollständige UI
class MainActivity : AppCompatActivity() {
 private lateinit var binding: ActivityMainBinding
 private lateinit var connection: SAMPConnection
 
 override fun onCreate(savedInstanceState: Bundle?) {
 super.onCreate(savedInstanceState)
 binding = ActivityMainBinding.inflate(layoutInflater)
 setContentView(binding.root)
 
 setupUI()
 checkPermissions()
 }
 
 private fun setupUI() {
 binding.apply {
 // Server Info Card
 serverNameText.text = "Dein Server Name"
 serverIPText.text = "127.0.0.1:7777"
 playersOnlineText.text = "0/100"
 
 // Connect Button
 connectButton.setOnClickListener {
 val username = usernameInput.text.toString()
 val password = passwordInput.text.toString()
 
 if (validateInput(username, password)) {
 connectToServer(username, password)
 }
 }
 
 // Settings Button
 settingsButton.setOnClickListener {
 showSettings()
 }
 }
 }
 
 private fun connectToServer(username: String, password: String) {
 // Loading anzeigen
 binding.connectButton.isEnabled = false
 binding.progressBar.visibility = View.VISIBLE
 
 // Connection in Background Thread
 lifecycleScope.launch(Dispatchers.IO) {
 val result = connection.connect(username, password)
 
 withContext(Dispatchers.Main) {
 

[Content truncated...]
