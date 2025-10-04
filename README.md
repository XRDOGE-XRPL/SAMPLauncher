# SA-MP Android Launcher

Ein moderner Android Launcher für San Andreas Multiplayer (SA-MP), entwickelt mit Kotlin und Material Design 3.

## 📱 Features

- **Moderne Android-Architektur**: Kotlin, Coroutines, Material Design 3
- **GTA:SA Integration**: Automatische Erkennung der GTA:SA Installation
- **SA-MP Protocol Support**: Custom UDP Socket Implementation für SA-MP Server
- **Server Query System**: Abrufen von Server-Informationen (Spielerzahl, Ping, etc.)
- **Benutzerfreundliche UI**: Material Design 3 mit Cards, TextFields und Buttons
- **Permission Management**: Runtime-Berechtigungen für Android 6.0+
- **Coroutines**: Asynchrone Netzwerk-Operationen

## 🛠️ Technologie-Stack

### Android Development
- **Sprache**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle (Kotlin DSL)

### Dependencies
- **AndroidX Core & AppCompat**
- **Material Design 3**: UI-Komponenten
- **Kotlin Coroutines**: Asynchrone Operationen
- **OkHttp3**: HTTP/HTTPS Kommunikation
- **Gson**: JSON Parsing
- **Room Database**: Lokale Datenspeicherung
- **Navigation Component**: Fragment-Navigation
- **Lifecycle Components**: ViewModels und LiveData

## 📂 Projekt-Struktur

```
SAMPLauncher/
├── app/
│   ├── src/main/
│   │   ├── java/com/xrdoge/samplauncher/
│   │   │   ├── MainActivity.kt
│   │   │   ├── network/
│   │   │   │   ├── SAMPConnection.kt
│   │   │   │   ├── ServerQuery.kt
│   │   │   │   └── PacketHandler.kt
│   │   │   ├── ui/
│   │   │   │   ├── ServerListFragment.kt
│   │   │   │   ├── LoginFragment.kt
│   │   │   │   └── GameFragment.kt
│   │   │   ├── data/
│   │   │   │   └── models/
│   │   │   │       ├── ServerInfo.kt
│   │   │   │       └── ConnectionResult.kt
│   │   │   └── utils/
│   │   │       ├── GTAPathFinder.kt
│   │   │       └── PermissionManager.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── drawable/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

## 🚀 Build-Anleitung

### Voraussetzungen
1. **Android Studio** (neueste Version)
2. **JDK 8 oder höher**
3. **Android SDK** (API Level 24-34)

### Build-Schritte

1. **Repository klonen**
   ```bash
   git clone https://github.com/XRDOGE-XRPL/SAMPLauncher.git
   cd SAMPLauncher
   ```

2. **Projekt in Android Studio öffnen**
   - Android Studio starten
   - "Open an Existing Project" wählen
   - SAMPLauncher-Ordner auswählen

3. **Gradle Sync**
   - Android Studio führt automatisch einen Gradle Sync durch
   - Bei Problemen: File → Sync Project with Gradle Files

4. **Build durchführen**
   - Build → Make Project (Ctrl+F9)
   - Oder: Build → Build Bundle(s) / APK(s) → Build APK(s)

5. **App installieren**
   - USB-Debugging auf Android-Gerät aktivieren
   - Gerät per USB verbinden
   - Run → Run 'app' (Shift+F10)

## 📝 Kern-Komponenten

### GTAPathFinder
Utility-Klasse zum Erkennen der GTA:SA Installation:
```kotlin
if (GTAPathFinder.isGTAInstalled(context)) {
    // GTA:SA ist installiert
    val path = GTAPathFinder.getGTAPath()
}
```

### SAMPConnection
Handler für SA-MP Server-Verbindungen:
```kotlin
val connection = SAMPConnection("127.0.0.1", 7777)
val result = connection.connect(username, password)

when (result) {
    is ConnectionResult.Success -> // Verbunden
    is ConnectionResult.Failed -> // Fehler: result.reason
}
```

### ServerQuery
Abfrage von Server-Informationen:
```kotlin
val serverQuery = ServerQuery()
val serverInfo = serverQuery.getServerInfo("127.0.0.1", 7777)
serverInfo?.let {
    println("${it.hostname}: ${it.playersOnline}/${it.maxPlayers}")
}
```

### PermissionManager
Verwaltung von Runtime-Berechtigungen:
```kotlin
if (!PermissionManager.hasRequiredPermissions(context)) {
    PermissionManager.requestPermissions(activity)
}
```

## 🔧 Konfiguration

### Server-Einstellungen
Die Standard-Server-Einstellungen können in `MainActivity.kt` angepasst werden:
```kotlin
private val defaultServerIP = "127.0.0.1"
private val defaultServerPort = 7777
```

## 📱 Berechtigungen

Die App benötigt folgende Berechtigungen:
- **INTERNET**: Netzwerk-Kommunikation mit SA-MP Servern
- **ACCESS_NETWORK_STATE**: Netzwerk-Status prüfen
- **READ_EXTERNAL_STORAGE**: GTA:SA Dateien lesen (Android < 13)
- **READ_MEDIA_IMAGES/VIDEO**: GTA:SA Dateien lesen (Android 13+)

## 🎨 UI-Design

Die App verwendet **Material Design 3** mit folgenden Komponenten:
- MaterialCardView für Server-Info und Login
- TextInputLayout mit OutlinedBox-Style
- MaterialButton für Actions
- CoordinatorLayout mit AppBarLayout
- FloatingActionButton für Einstellungen

## 🔍 SA-MP Protocol

Die App implementiert das SA-MP Protocol für:
1. **Server Query**: Abrufen von Server-Informationen (Opcode 'i')
2. **Connection Handshake**: Verbindung zum Server aufbauen
3. **Packet Handling**: Verarbeitung von SA-MP Paketen

### SA-MP Packet-Struktur
```
Query Packet:
- 4 bytes: "SAMP"
- 4 bytes: IP Address (Octets)
- 2 bytes: Port (Little Endian)
- 1 byte: Opcode
```

## 🧪 Testing

Das Projekt nutzt:
- **JUnit 4**: Unit-Tests
- **Espresso**: UI-Tests
- **AndroidX Test**: Android-Testing-Framework

## 🐛 Bekannte Probleme

1. **GTA:SA nicht gefunden**: Storage-Berechtigungen müssen erteilt werden
2. **Server antwortet nicht**: Firewall-Einstellungen oder falscher Port
3. **Connection Timeout**: SA-MP Protocol muss korrekt implementiert sein

## 📚 Ressourcen

- [SA-MP Wiki](https://wiki.sa-mp.com)
- [Android Developers](https://developer.android.com)
- [Material Design 3](https://m3.material.io)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

## 📄 Lizenz

Dieses Projekt ist Open Source. Bitte beachten Sie die Lizenzbedingungen von SA-MP und GTA:SA.

## 👥 Mitwirken

Contributions sind willkommen! Bitte erstellen Sie einen Pull Request oder öffnen Sie ein Issue.

## 🔗 Links

- [GitHub Repository](https://github.com/XRDOGE-XRPL/SAMPLauncher)
- [SA-MP Official](https://www.sa-mp.com)

---

**Hinweis**: Diese App benötigt eine installierte Version von GTA: San Andreas auf dem Android-Gerät.