package com.pvz.plantsvszombies.Multiplayer.Engines;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.*;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Multiplayer.Events.*;
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
//            waitForClientsReady();

            _gameStarted = true;
            _aliveClients.addAll(_connectedClients);

            // Stop accepting new client connections
            networkManager.stopAcceptingConnections();

            System.out.println("Game started with " + currentPlayers + " players!");

            // Notify all clients that game is starting
            GameStartEvent startEvent = new GameStartEvent(tick, currentPlayers);
            networkManager.sendEvent(startEvent);
        }
    }

    private void waitForClients() {
        System.out.println("Waiting for " + _requiredClients + " clients to connect...");
        while ((long) _connectedClients.size() < _requiredClients && !_gameEnded) {
            try {
                // Process events to populate _connectedClients
                networkManager.processEvents();
                Thread.sleep(1000);
                System.out.println("Connected clients: " + networkManager.getConnectedClientCount() + "/" + _requiredClients);
            } catch (InterruptedException e) {
                return;
            }
        }
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
        long currentTime = (long) getMilliseconds();

        if (currentTime == _wave_4_Start.toMillis() && _currentWave < 4) {
            _currentWave = 4;
            broadcastWaveChange(4, (long) currentTime);
        } else if (currentTime == _wave_3_Start.toMillis() && _currentWave < 3) {
            _currentWave = 3;
            broadcastWaveChange(3, (long) currentTime);
        } else if (currentTime == _wave_2_Start.toMillis() && _currentWave < 2) {
            _currentWave = 2;
            broadcastWaveChange(2, (long) currentTime);
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
        var row = _zombieSpawnRandom.nextInt(0, _rows);
        spawnZombie(row, _columns - 1, zombieType);
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
        if (event instanceof ClientStatusEvent statusEvent) {
            String clientId = statusEvent.getClientId();
            ClientStatusEvent.ClientStatus status = statusEvent.getStatus();

            switch (status) {
                case CONNECTED -> {
                    if (!_connectedClients.contains(clientId)) {
                        _connectedClients.add(clientId);
                        System.out.println("Client connected: " + clientId + " (" + _connectedClients.size() + "/" + _requiredClients + ")");
                    }
                }
                case LOST -> {
                    if (_gameStarted) { // Only handle loss after game has started
                        _aliveClients.remove(clientId);
                        System.out.println("Client lost: " + clientId + " (Alive: " + _aliveClients.size() + "/" + _connectedClients.size() + ")");
                        checkGameEndConditions();
                    }
                }
                case WON -> {
                    System.out.println("Client completed all waves: " + clientId);
                    // First client to complete all waves wins
                    if (_gameStarted && !_gameEnded) {
                        endGame(clientId, GameEndEvent.EndReason.WAVES_COMPLETED);
                    }
                }
                case DISCONNECTED -> {
                    _aliveClients.remove(clientId);
                    _connectedClients.remove(clientId);
                    _clientPlants.remove(clientId);
                    System.out.println("Client disconnected: " + clientId + " (" + _connectedClients.size() + "/" + _requiredClients + ")");

                    // Only check end conditions if game has started
                    if (_gameStarted) {
                        checkGameEndConditions();
                    } else if (_connectedClients.isEmpty() && networkManager.getConnectedClientCount() == 0) {
                        // Only shutdown if both our tracking list AND network manager show no clients
                        // This prevents premature shutdown due to tracking bugs
                        endGame(null, GameEndEvent.EndReason.SERVER_SHUTDOWN);
                    }
                }
            }
        } else if (event instanceof ClientReadyEvent readyEvent) {
            String clientId = readyEvent.getClientId();
            if (!_connectedClients.contains(clientId)) {
                _connectedClients.add(clientId);
                _clientPlants.put(clientId, readyEvent.getSelectedPlants());

                // Update Map Block Coordinates:
                _currentMap = readyEvent.getMapObject();

                System.out.println("Client ready: " + clientId + " with plants: " + readyEvent.getSelectedPlants());
                System.out.println("Ready clients: " + _connectedClients.size() + "/" + _requiredClients);
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

        GameEndEvent event = new GameEndEvent(tick, winnerId, winnerMessage, reason, (long) getMilliseconds());
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

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

    @Override
    public void spawnZombie(int row, int col, AbstractZombieGameObject.ZombieType zombieType) {
        String zombieObjectId = "Zombie" + UUID.randomUUID();
        var block = _currentMap.getBlock(row, col);
        Coordinate coordinate = block.getCenterCoordinate();
        AbstractZombieGameObject zombie = switch (zombieType) {
            case NORMAL_ZOMBIE -> NormalZombieGameObject.createNormalZombieGameObject(this, zombieObjectId,
                    coordinate.copy(), row, col);
            case CONE_HEAD_ZOMBIE -> ConeHeadZombieGameObject.createConeHeadZombieGameObject(this, zombieObjectId,
                    coordinate.copy(), row, col);
            case IMP_ZOMBIE -> ImpZombieGameObject.createImpZombieGameObject(this, zombieObjectId,
                    coordinate.copy(), row, col);
            case SCREEN_DOOR_ZOMBIE -> ScreenDoorZombieGameObject.createScreenDoorZombieGameObject(this, zombieObjectId,
                    coordinate.copy(), row, col); // Here if you pass 'coordinate' as coordinate.copy(), all zombies will share a same coordinate
        };

        this._gameObjects.add(zombie);
        for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) {
            eventSubscriber._notify(zombie);
        }

        // Create and broadcast zombie spawn event
        ZombieSpawnEvent event = new ZombieSpawnEvent(
                tick, row, col, zombieType.name(), zombieObjectId, coordinate
        );

        networkManager.sendEvent(event);

    }

    // Getters for server monitoring
    public boolean isGameStarted() {
        return _gameStarted;
    }

    public boolean isGameEnded() {
        return _gameEnded;
    }

    public int getCurrentWave() {
        return _currentWave;
    }

    public int getConnectedClientCount() {
        return _connectedClients.size();
    }

    public int getAliveClientCount() {
        return _aliveClients.size();
    }

    public int getReadyClientCount() {
        return _connectedClients.size();
    }

    public int getRequiredClients() {
        return _requiredClients;
    }
}
