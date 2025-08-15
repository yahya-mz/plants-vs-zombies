# Plants vs Zombies Multiplayer System Documentation

## Overview
This document explains how the multiplayer system works in the Plants vs Zombies game. The system uses a client-server architecture where one player hosts the game and others join.

## Architecture

### Components
- **ServerGameEngine**: Handles game logic, zombie spawning, and client coordination
- **ClientGameEngine**: Manages local game state and communicates with server
- **NetworkManager**: Handles network communication using Java serialization
- **MultiplayerMenuView**: Main menu for hosting/joining games
- **MultiplayerPickingStage**: Plant selection interface
- **MultiplayerGameView**: Main game interface for clients

## Flow Diagram

```
1. Host Setup
   ├── User clicks "Start Server"
   ├── ServerGameEngine starts
   ├── Waits for required clients
   └── Auto-joins host as player (optional)

2. Client Join
   ├── User clicks "Connect to Game"
   ├── MultiplayerPickingStage opens
   ├── User selects 6 plants
   └── User clicks "Start"

3. Connection Phase
   ├── ClientGameEngine created
   ├── Client connects to server
   ├── Client sends "CONNECTED" status
   └── Server adds client to _connectedClients

4. Ready Phase
   ├── Client sends "READY" status with plant selection
   ├── Server adds client to _readyClients
   ├── Server waits for all clients to be ready
   └── Game starts when all clients ready

5. Game Phase
   ├── Server spawns zombies and manages game state
   ├── Clients receive events and update local state
   ├── Game continues until win/loss conditions
   └── Server broadcasts game end
```

## Key Classes and Their Responsibilities

### ServerGameEngine
- **Purpose**: Central game authority
- **Key Methods**:
  - `start()`: Initializes server and waits for clients
  - `waitForClients()`: Waits for required number of connections
  - `waitForClientsReady()`: Waits for all clients to send ready status
  - `update()`: Main game loop (zombie spawning, wave progression)
  - `handleClientEvent()`: Processes client status and ready events

### ClientGameEngine
- **Purpose**: Local game state and server communication
- **Key Methods**:
  - `start()`: Connects to server and sends "CONNECTED" status
  - `update()`: Processes server events and updates local game
  - `sendReadyStatus()`: Sends plant selection to server
  - `handleServerEvent()`: Processes incoming server events

### NetworkManager
- **Purpose**: Handles network communication
- **Key Methods**:
  - `start()`: Establishes connection
  - `processEvents()`: Processes incoming/outgoing events
  - `sendEvent()`: Sends events to remote peer

## Event System

### Client → Server Events
- **ClientStatusEvent**: Connection status ("CONNECTED", "DISCONNECTED", "PLAYING", "LOST", "WON")
- **ClientReadyEvent**: Plant selection and readiness status

### Server → Client Events
- **GameStartEvent**: Signals game beginning
- **ZombieSpawnEvent**: New zombie spawns
- **SunDropEvent**: Sun drops from sky
- **WaveChangeEvent**: Wave progression
- **GameEndEvent**: Game completion

## Data Flow

### Connection Flow
1. Client connects via socket
2. Client sends "CONNECTED" status
3. Server adds client to `_connectedClients`
4. Server waits for required number of clients

### Ready Flow
1. Client selects 6 plants
2. Client sends "READY" status with plant list
3. Server adds client to `_readyClients`
4. Server waits for all clients to be ready
5. Game starts when `_readyClients.size() == _requiredClients`

### Game Flow
1. Server runs main game loop at 30 FPS
2. Server spawns zombies, drops sun, progresses waves
3. Server broadcasts events to all clients
4. Clients receive events and update local state
5. Clients send periodic "PLAYING" status

## Critical Fixes Applied

### 1. Event Processing During Wait Loops
- **Problem**: Server couldn't process client events while waiting
- **Solution**: Added `networkManager.processEvents()` calls in wait loops

### 2. Premature Server Shutdown
- **Problem**: Server shutdown when `_connectedClients` was empty
- **Solution**: Added double-check with network manager before shutdown

### 3. Missing Client Game Loop
- **Problem**: Client couldn't process server events
- **Solution**: Added game loop in MultiplayerGameView

### 4. Duplicate Client Connections
- **Problem**: Multiple start() calls created duplicate connections
- **Solution**: Added connection check in ClientGameEngine.start()

### 5. Auto-Join After Game Start
- **Problem**: Host picking stage appeared after game started
- **Solution**: Added game start check before auto-join

## Configuration

### Server Settings
- **Port**: 12345 (hardcoded)
- **Player Count**: 2-4 players (configurable)
- **Game Modes**: Day/Night
- **Host Participation**: Optional

### Client Settings
- **Server Address**: Configurable (default: localhost)
- **Plant Selection**: Exactly 6 plants required
- **Connection Timeout**: 5 seconds

## Troubleshooting

### Common Issues
1. **"Ready clients: 0/2"**: Client ready events not being processed
2. **Server disconnects**: Check for empty client lists
3. **Client can't receive events**: Verify game loop is running
4. **Picking stage appears after game start**: Auto-join timing issue

### Debug Steps
1. Check server logs for client connections
2. Verify client ready status is sent
3. Ensure game loop is running on client
4. Check network connectivity

## Future Improvements

### Potential Enhancements
1. **Lobby System**: Better client management
2. **Reconnection**: Handle client disconnections gracefully
3. **Game State Sync**: Better synchronization of game objects
4. **Performance**: Optimize event processing and network calls
5. **Error Handling**: More robust error handling and recovery

## Testing

### Test Scenarios
1. **Host + 1 Client**: Basic 2-player game
2. **Host + 2 Clients**: 3-player game
3. **Client Disconnection**: Handle client leaving
4. **Server Restart**: Reconnection after server restart
5. **Network Issues**: Handle network interruptions

### Success Criteria
- All clients connect successfully
- Plant selection is properly synchronized
- Game starts when all clients ready
- Zombies spawn and move correctly
- Game ends properly with win/loss conditions