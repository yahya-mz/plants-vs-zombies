package com.pvz.plantsvszombies.Multiplayer.Engines;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Multiplayer.Events.ClientStatusEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.ClientReadyEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.GameEndEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.GameStartEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.SharedEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.SunDropEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.WaveChangeEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.ZombieSpawnEvent;
import com.pvz.plantsvszombies.Multiplayer.Network.ServerNetworkManager;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Server-side game engine that manages multiplayer game state
 */
public class ServerGameEngine extends com.pvz.plantsvszombies.Domain.Engines.DayEngine {
    private final ServerNetworkManager networkManager;
    private final int _requiredClients;
    
    // Track client status
    private final CopyOnWriteArrayList<String> _connectedClients = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<String> _aliveClients = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<String> _readyClients = new CopyOnWriteArrayList<>();
    private final Map<String, java.util.List<String>> _clientPlants = new HashMap<>(); // Client ID -> Plant types
    
    // Game state
    private boolean _gameStarted = false;
    private boolean _gameEnded = false;
    private int _currentWave = 0;
    
    // Wave timing
    private final Duration _wave_2_Start = Duration.ofSeconds(30);
    private final Duration _wave_3_Start = Duration.ofSeconds(60);
    private final Duration _wave_4_Start = Duration.ofSeconds(90);
    
    // Random generators
    private final Random _zombieSpawnRandom = new Random();
    private final Random _zombieTypeRandom = new Random();
    private final Random _skyDroppingRandom = new Random();
    
    public ServerGameEngine(double windowWidth, double windowHeight, int requiredClients) {
        super(windowWidth, windowHeight);
        this._requiredClients = requiredClients;
        this._gameObjects = new CopyOnWriteArrayList<>();
        
        // Initialize network manager
        this.networkManager = new ServerNetworkManager(12345, requiredClients);
        
        // Listen for client status events
        this.networkManager.addEventListener(this::handleClientEvent);
    }
    
    @Override
    public void start() {
        System.out.println("Starting server game engine...");
        networkManager.start();
        
        // Wait for required number of clients
        waitForClients();
        
        // Check if we have enough players (including host if they're playing)
        int currentPlayers = networkManager.getConnectedClientCount();
        System.out.println("Current players: " + currentPlayers + "/" + _requiredClients);
        
        if (currentPlayers >= _requiredClients) {
            // Wait for all clients to be ready with plant selection
            waitForClientsReady();
            
            _gameStarted = true;
            _aliveClients.addAll(_connectedClients);
            
            // Stop accepting new client connections
            networkManager.stopAcceptingConnections();
            
            System.out.println("Game started with " + currentPlayers + " players!");
            
            // Broadcast game start
            broadcastWaveChange(1, (long)getMilliseconds());
            
            // Notify all clients that game is starting
            GameStartEvent startEvent = new GameStartEvent(tick, currentPlayers);
            networkManager.sendEvent(startEvent);
        }
    }
    
    private void waitForClients() {
        System.out.println("Waiting for " + _requiredClients + " clients to connect...");
        while (networkManager.getConnectedClientCount() < _requiredClients && !_gameEnded) {
            try {
                System.out.println("DEBUG: About to process events...");
                // Process events to populate _connectedClients
                networkManager.processEvents();
                System.out.println("DEBUG: Events processed");
                Thread.sleep(1000);
                System.out.println("Connected clients: " + networkManager.getConnectedClientCount() + "/" + _requiredClients);
                System.out.println("DEBUG: _connectedClients size: " + _connectedClients.size());
                System.out.println("DEBUG: _connectedClients contents: " + _connectedClients);
                System.out.println("DEBUG: Network manager connected count: " + networkManager.getConnectedClientCount());
            } catch (InterruptedException e) {
                return;
            }
        }
    }
    
    private void waitForClientsReady() {
        System.out.println("Waiting for all clients to be ready with plant selection...");
        while (_readyClients.size() < _requiredClients && !_gameEnded) {
            try {
                // Process events to populate _readyClients
                networkManager.processEvents();
                Thread.sleep(1000);
                System.out.println("Ready clients: " + _readyClients.size() + "/" + _requiredClients);
                System.out.println("DEBUG: _connectedClients size: " + _connectedClients.size() + ", _readyClients size: " + _readyClients.size());
            } catch (InterruptedException e) {
                return;
            }
        }
        System.out.println("All clients are ready! Starting game...");
    }
    
    @Override
    public void update() {
        // Always process incoming client events, even before game starts
        networkManager.processEvents();
        
        if (!_gameStarted || _gameEnded) {
            return;
        }
        
        // Check for game end conditions
        checkGameEndConditions();
        
        if (_gameEnded) {
            return;
        }
        
        // Handle wave progression
        handleWaveProgression();
        
        // Handle sun dropping
        handleSunDropping();
        
        // Handle zombie spawning
        handleZombieSpawning();
        
        tick++;
    }
    
    private void handleWaveProgression() {
        long currentTime = (long)getMilliseconds();
        
        if (currentTime == _wave_4_Start.toMillis() && _currentWave < 4) {
            _currentWave = 4;
            broadcastWaveChange(4, (long)currentTime);
        } else if (currentTime == _wave_3_Start.toMillis() && _currentWave < 3) {
            _currentWave = 3;
            broadcastWaveChange(3, (long)currentTime);
        } else if (currentTime == _wave_2_Start.toMillis() && _currentWave < 2) {
            _currentWave = 2;
            broadcastWaveChange(2, (long)currentTime);
        }
    }
    
    private void handleSunDropping() {
        if (getMilliseconds() % 10000.0 == 0) { // Drop sun every 10 seconds
            dropSunFromSky();
        }
    }
    
    private void handleZombieSpawning() {
        switch (_currentWave) {
            case 0 -> {
                if (getMilliseconds() % 5000.0 == 0) { // Spawn zombie every 5 seconds
                    spawnRandomZombie(AbstractZombieGameObject.ZombieType.NORMAL_ZOMBIE);
                }
            }
            case 1 -> {
                if (getMilliseconds() % 4000.0 == 0) { // Spawn zombie every 4 seconds
                    AbstractZombieGameObject.ZombieType[] zombieTypes = {
                        AbstractZombieGameObject.ZombieType.NORMAL_ZOMBIE,
                        AbstractZombieGameObject.ZombieType.CONE_HEAD_ZOMBIE
                    };
                    spawnRandomZombie(zombieTypes[_zombieTypeRandom.nextInt(zombieTypes.length)]);
                }
            }
            case 2 -> {
                if (getMilliseconds() % 3000.0 == 0) { // Spawn zombie every 3 seconds
                    AbstractZombieGameObject.ZombieType[] zombieTypes = {
                        AbstractZombieGameObject.ZombieType.NORMAL_ZOMBIE,
                        AbstractZombieGameObject.ZombieType.CONE_HEAD_ZOMBIE,
                        AbstractZombieGameObject.ZombieType.SCREEN_DOOR_ZOMBIE
                    };
                    spawnRandomZombie(zombieTypes[_zombieTypeRandom.nextInt(zombieTypes.length)]);
                }
            }
            case 3 -> {
                if (getMilliseconds() % 2000.0 == 0) { // Spawn zombie every 2 seconds
                    var allTypes = AbstractZombieGameObject.ZombieType.values();
                    spawnRandomZombie(allTypes[_zombieTypeRandom.nextInt(allTypes.length)]);
                }
            }
        }
    }
    
    private void spawnRandomZombie(AbstractZombieGameObject.ZombieType zombieType) {
        int row = _zombieSpawnRandom.nextInt(0, _rows);
        int column = _columns - 1;
        String zombieId = "Server_Zombie_" + UUID.randomUUID();
        
        // Calculate spawn coordinate
        Coordinate spawnCoordinate = new Coordinate(
            column * 90.0 + 45.0, // MapBlock.BLOCK_SIZE center
            row * 90.0 + 45.0
        );
        
        // Create and broadcast zombie spawn event
        ZombieSpawnEvent event = new ZombieSpawnEvent(
            tick, row, column, zombieType.name(), zombieId, spawnCoordinate
        );
        
        networkManager.sendEvent(event);
        System.out.println("Spawned zombie: " + zombieType.name() + " at row " + row);
    }
    
    private void dropSunFromSky() {
        String sunId = "Server_Sun_" + UUID.randomUUID();
        Coordinate dropCoordinate = new Coordinate(
            _skyDroppingRandom.nextDouble(0.2 * _windowWidth, 0.8 * _windowWidth),
            0
        );
        
        // Create and broadcast sun drop event
        SunDropEvent event = new SunDropEvent(tick, sunId, dropCoordinate, 25);
        networkManager.sendEvent(event);
        System.out.println("Sun dropped at: " + dropCoordinate.x());
    }
    
    private void broadcastWaveChange(int wave, long startTime) {
        WaveChangeEvent event = new WaveChangeEvent(tick, wave, startTime);
        networkManager.sendEvent(event);
        System.out.println("Wave " + wave + " started!");
    }
    
    private void handleClientEvent(SharedEvent event) {
        System.out.println("DEBUG: Server received event: " + event.getEventType() + " from " + event.getClass().getSimpleName());
        
        if (event instanceof ClientStatusEvent statusEvent) {
            String clientId = statusEvent.getClientId();
            String status = statusEvent.getStatus();
            System.out.println("DEBUG: ClientStatusEvent - clientId: " + clientId + ", status: " + status);
            
            switch (status) {
                case "CONNECTED" -> {
                    System.out.println("DEBUG: Processing CONNECTED status for client: " + clientId);
                    if (!_connectedClients.contains(clientId)) {
                        _connectedClients.add(clientId);
                        System.out.println("Client connected: " + clientId + " (" + _connectedClients.size() + "/" + _requiredClients + ")");
                        System.out.println("DEBUG: _connectedClients now contains: " + _connectedClients);
                    } else {
                        System.out.println("DEBUG: Client " + clientId + " already in _connectedClients");
                    }
                }
                case "LOST" -> {
                    if (_gameStarted) { // Only handle loss after game has started
                        _aliveClients.remove(clientId);
                        System.out.println("Client lost: " + clientId + " (Alive: " + _aliveClients.size() + "/" + _connectedClients.size() + ")");
                        checkGameEndConditions();
                    }
                }
                case "WON" -> {
                    System.out.println("Client completed all waves: " + clientId);
                    // First client to complete all waves wins
                    if (_gameStarted && !_gameEnded) {
                        endGame(clientId, GameEndEvent.EndReason.WAVES_COMPLETED);
                    }
                }
                case "DISCONNECTED" -> {
                    _connectedClients.remove(clientId);
                    _aliveClients.remove(clientId);
                    _readyClients.remove(clientId);
                    _clientPlants.remove(clientId);
                    System.out.println("Client disconnected: " + clientId + " (" + _connectedClients.size() + "/" + _requiredClients + ")");
                    
                    // Only check end conditions if game has started
                    if (_gameStarted) {
                        checkGameEndConditions();
                    } else if (_connectedClients.isEmpty() && networkManager.getConnectedClientCount() == 0) {
                        // Only shutdown if both our tracking list AND network manager show no clients
                        // This prevents premature shutdown due to tracking bugs
                        System.out.println("DEBUG: All clients disconnected, shutting down server");
                        endGame(null, GameEndEvent.EndReason.SERVER_SHUTDOWN);
                    } else if (_connectedClients.isEmpty() && networkManager.getConnectedClientCount() > 0) {
                        System.out.println("DEBUG: _connectedClients is empty but network manager shows " + networkManager.getConnectedClientCount() + " clients - tracking bug detected");
                    }
                }
            }
        } else if (event instanceof ClientReadyEvent readyEvent) {
            String clientId = readyEvent.getClientId();
            System.out.println("DEBUG: Processing ClientReadyEvent for client: " + clientId + " with plants: " + readyEvent.getSelectedPlants());
            if (!_readyClients.contains(clientId)) {
                _readyClients.add(clientId);
                _clientPlants.put(clientId, readyEvent.getSelectedPlants());
                System.out.println("Client ready: " + clientId + " with plants: " + readyEvent.getSelectedPlants());
                System.out.println("Ready clients: " + _readyClients.size() + "/" + _requiredClients);
                System.out.println("DEBUG: _readyClients now contains: " + _readyClients);
            } else {
                System.out.println("DEBUG: Client " + clientId + " already in _readyClients");
            }
        }
    }
    
    private void checkGameEndConditions() {
        if (_gameStarted && !_gameEnded) {
            if (_aliveClients.isEmpty()) {
                // All clients lost
                endGame(null, GameEndEvent.EndReason.PLAYER_LOST);
            } else if (_aliveClients.size() == 1) {
                // Only one player left - they win!
                endGame(_aliveClients.get(0), GameEndEvent.EndReason.LAST_SURVIVOR);
            }
        }
    }
    
    private String findWinnerClient() {
        // Return the first alive client (simplified - could be enhanced with more criteria)
        return _aliveClients.isEmpty() ? null : _aliveClients.get(0);
    }
    
    private void endGame(String winnerId, GameEndEvent.EndReason reason) {
        if (_gameEnded) return;
        
        _gameEnded = true;
        String winnerName = winnerId != null ? winnerId : "No winner";
        
        // Create descriptive winner message
        String winnerMessage = switch (reason) {
            case WAVES_COMPLETED -> winnerName + " completed all waves first!";
            case LAST_SURVIVOR -> winnerName + " is the last survivor!";
            case PLAYER_LOST -> "All players were defeated!";
            case DISCONNECTION -> "Connection lost!";
            case SERVER_SHUTDOWN -> "Server shutting down...";
        };
        
        GameEndEvent event = new GameEndEvent(tick, winnerId, winnerMessage, reason, (long)getMilliseconds());
        networkManager.sendEvent(event);
        
        System.out.println("Game ended - " + winnerMessage + " (Reason: " + reason + ")");
        
        // Stop server after a delay
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Give clients time to receive the event
                stop();
            } catch (InterruptedException e) {
                // Ignore
            }
        }).start();
    }
    
    public void stop() {
        _gameEnded = true;
        if (networkManager != null) {
            networkManager.stop();
        }
        System.out.println("Server stopped.");
    }
    
    @Override
    public void load() {
        // Server doesn't load game state
    }
    
    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }
    
    // Getters for server monitoring
    public boolean isGameStarted() { return _gameStarted; }
    public boolean isGameEnded() { return _gameEnded; }
    public int getCurrentWave() { return _currentWave; }
    public int getConnectedClientCount() { return _connectedClients.size(); }
    public int getAliveClientCount() { return _aliveClients.size(); }
    public int getReadyClientCount() { return _readyClients.size(); }
    public int getRequiredClients() { return _requiredClients; }
}
