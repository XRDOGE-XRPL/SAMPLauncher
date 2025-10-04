# SA-MP Android Launcher - Development Documentation

## Architecture Overview

This Android application follows modern Android development practices with a layered architecture:

### Layers

1. **UI Layer** (`ui/` package)
   - Fragments for different screens
   - Activities for hosting fragments
   - Material Design 3 components

2. **Network Layer** (`network/` package)
   - SAMPConnection: Handles server connections
   - ServerQuery: Queries server information
   - PacketHandler: Utility for packet manipulation

3. **Data Layer** (`data/` package)
   - Models: Data classes for domain objects
   - Repository: Data access abstraction (planned)
   - Database: Room database setup (planned)

4. **Utils Layer** (`utils/` package)
   - GTAPathFinder: GTA:SA installation detection
   - PermissionManager: Runtime permission handling

## SA-MP Protocol Implementation

### Connection Flow

1. Client creates connection packet with username
2. Server responds with connection result (Success/Failed)
3. On success, client can send game packets

### Query Protocol

1. Build query packet with server IP/port and opcode
2. Send UDP packet to server
3. Parse response for server information

### Packet Structure

```
Connection Packet:
+------+--------+----------+----------+
| SAMP | Version| Name Len | Username |
+------+--------+----------+----------+
  4B      2B        1B        Variable

Query Packet:
+------+----+------+--------+
| SAMP | IP | Port | Opcode |
+------+----+------+--------+
  4B    4B    2B       1B
```

## Material Design Implementation

The app uses Material Design 3 with:
- Cards for content grouping
- Outlined text fields for input
- Floating action buttons for primary actions
- AppBar with toolbar
- Color theming from Material guidelines

## Kotlin Coroutines Usage

Network operations are performed asynchronously using Kotlin Coroutines:

```kotlin
lifecycleScope.launch {
    val result = withContext(Dispatchers.IO) {
        // Network operation
    }
    // Update UI on main thread
}
```

## Future Enhancements

1. **Server List Management**
   - Room database for favorite servers
   - Server history tracking
   - Custom server addition

2. **Enhanced UI**
   - Dark theme support
   - Custom color schemes per state
   - Animated transitions

3. **Advanced Features**
   - In-app chat
   - Server browser
   - Player statistics
   - Gamepad mapping

4. **Backend Integration**
   - MySQL connection for account management
   - REST API for server data
   - WebSocket for real-time updates

## Building for Production

1. Update version in `app/build.gradle.kts`
2. Generate signed APK with release keystore
3. Test on multiple Android versions
4. Submit to distribution platform

## Testing Strategy

1. **Unit Tests**
   - Network packet creation/parsing
   - Permission checking logic
   - GTA path detection

2. **Integration Tests**
   - Server connection flow
   - Database operations
   - Fragment navigation

3. **UI Tests**
   - User login flow
   - Server query display
   - Error message handling
