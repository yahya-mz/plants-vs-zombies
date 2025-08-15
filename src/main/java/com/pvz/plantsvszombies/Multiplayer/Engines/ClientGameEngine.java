package com.pvz.plantsvszombies.Multiplayer.Engines;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.*;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Multiplayer.Events.ClientReadyEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.ClientStatusEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.GameEndEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.GameStartEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.SharedEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.SunDropEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.WaveChangeEvent;
import com.pvz.plantsvszombies.Multiplayer.Events.ZombieSpawnEvent;
import com.pvz.plantsvszombies.Multiplayer.Network.ClientNetworkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Client-side game engine that handles local events (planting, bullets) 
 * and listens for shared events from the server
 */
public class ClientGameEngine extends GameEngine {
    private final ClientNetworkManager networkManager;
    private String clientId;
    private int _currentWave = 1;
    private boolean _gameStarted = false;
    private boolean _gameEnded = false;
    private boolean _playerLost = false;
    private String _gameMode; // "day" or "night"
    
    // Track game state for win condition
    private int _zombiesKilled = 0;
    private long _gameStartTime = 0;
    
    // Plant selection
    private ArrayList<AbstractPlantGameObject.PlantType> _selectedPlants;
    
    public ClientGameEngine(double windowWidth, double windowHeight, String serverHost, String gameMode) {
        this._windowWidth = windowWidth;
        this._windowHeight = windowHeight;
        this._gameMode = gameMode;
        this._gameObjects = new CopyOnWriteArrayList<>();
        
        // Initialize network manager first
        this.networkManager = new ClientNetworkManager(serverHost);
        
        // Generate client ID and set it in both places
        this.clientId = "Client_" + java.util.UUID.randomUUID().toString().substring(0, 8);
        this.networkManager.setClientId(this.clientId);
        
        // Listen for server events
        this.networkManager.addEventListener(this::handleServerEvent);
        
        System.out.println("Client game engine initialized: " + clientId);
    }
    
    @Override
    public void start() {
        // Prevent multiple starts
        if (networkManager.isConnected()) {
            System.out.println("Client game engine already started and connected");
            return;
        }
        
        System.out.println("Starting client game engine...");
        
        try {
            networkManager.start();
            initMap();
            
            // Send connection status
            sendClientStatus("CONNECTED");
            
            System.out.println("Client connected and waiting for game to start...");
        } catch (Exception e) {
            System.err.println("Failed to start client: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void update() {
        if (_gameEnded) {
            return;
        }
        
        // Process incoming server events
        networkManager.processEvents();
        
        if (!_gameStarted) {
            return;
        }
        
        // Update all local game objects
        synchronized (_gameObjects) {
            for (AbstractGameObject gameObject : _gameObjects) {
                gameObject.update();
                
                // Clean up objects that are out of bounds
                if (gameObject.getCoordinate().x() > this._windowWidth
                        || gameObject.getCoordinate().y() > this._windowHeight
                        || gameObject.getCoordinate().x() < 0
                        || gameObject.getCoordinate().y() < 0) {
                    gameObject.dispose();
                }
            }
        }
        
        // Check for loss condition
        checkLossCondition();
        
        // Check for win condition
        checkWinCondition();
        
        // Send periodic status updates
        if (tick % (GlobalSettings.FPS * 5) == 0) { // Every 5 seconds
            sendClientStatus("PLAYING");
        }
        
        tick++;
    }
    
    private void handleServerEvent(SharedEvent event) {
        switch (event) {
            case GameStartEvent startEvent -> handleGameStart(startEvent);
            case ZombieSpawnEvent zombieEvent -> handleZombieSpawn(zombieEvent);
            case SunDropEvent sunEvent -> handleSunDrop(sunEvent);
            case WaveChangeEvent waveEvent -> handleWaveChange(waveEvent);
            case GameEndEvent endEvent -> handleGameEnd(endEvent);
            default -> {
                // Ignore other event types
            }
        }
    }
    
    private void handleZombieSpawn(ZombieSpawnEvent event) {
        if (!_gameStarted) return;
        
        // Create zombie locally using server data
        AbstractZombieGameObject zombie = switch (event.getZombieType()) {
            case "NORMAL_ZOMBIE" -> NormalZombieGameObject.createNormalZombieGameObject(
                this, event.getZombieId(), event.getSpawnCoordinate(), event.getRow(), event.getColumn());
            case "CONE_HEAD_ZOMBIE" -> ConeHeadZombieGameObject.createConeHeadZombieGameObject(
                this, event.getZombieId(), event.getSpawnCoordinate(), event.getRow(), event.getColumn());
            case "IMP_ZOMBIE" -> ImpZombieGameObject.createImpZombieGameObject(
                this, event.getZombieId(), event.getSpawnCoordinate(), event.getRow(), event.getColumn());
            case "SCREEN_DOOR_ZOMBIE" -> ScreenDoorZombieGameObject.createScreenDoorZombieGameObject(
                this, event.getZombieId(), event.getSpawnCoordinate(), event.getRow(), event.getColumn());
            default -> throw new IllegalArgumentException("Unknown zombie type: " + event.getZombieType());
        };
        
        // Subscribe to zombie death events to track kills
        zombie.subscribeToDeathEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                _zombiesKilled++;
            }
        });
        
        this._gameObjects.add(zombie);
        
        // Notify visual engine
        for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) {
            eventSubscriber._notify(zombie);
        }
        
        System.out.println("Spawned zombie from server: " + event.getZombieType() + " at row " + event.getRow());
    }
    
    private void handleSunDrop(SunDropEvent event) {
        if (!_gameStarted) return;
        
        // Create sun object locally
        var sun = SunGameObject.createSunGameObject(this, event.getSunId(), event.getDropCoordinate());
        this._gameObjects.add(sun);
        
        // Notify visual engine
        for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) {
            eventSubscriber._notify(sun);
        }
        
        System.out.println("Sun dropped from server at: " + event.getDropCoordinate().x());
    }
    
    private void handleGameStart(GameStartEvent event) {
        _gameStarted = true;
        _gameStartTime = System.currentTimeMillis();
        System.out.println("🎮 Game starting with " + event.getPlayerCount() + " players!");
        
        // Send initial status
        sendClientStatus("PLAYING");
    }
    
    private void handleWaveChange(WaveChangeEvent event) {
        _currentWave = event.getNewWave();
        
        if (event.getNewWave() == 1 && !_gameStarted) {
            _gameStarted = true;
            _gameStartTime = System.currentTimeMillis();
            System.out.println("Game started! Wave 1 begins.");
        } else {
            System.out.println("Wave " + event.getNewWave() + " started!");
        }
    }
    
    private void handleGameEnd(GameEndEvent event) {
        _gameEnded = true;
        
        if (clientId.equals(event.getWinnerId())) {
            System.out.println("🎉 YOU WON! Congratulations!");
            win();
        } else {
            System.out.println("Game ended. Winner: " + event.getWinnerName());
            System.out.println("Reason: " + event.getEndReason());
        }
    }
    
    private void checkLossCondition() {
        if (_playerLost) return;
        
        // Check if any zombie reached the left side
        for (AbstractGameObject obj : _gameObjects) {
            if (obj instanceof AbstractZombieGameObject zombie) {
                if (zombie.getCoordinate().x() <= 50) { // Reached house
                    _playerLost = true;
                    sendClientStatus("LOST");
                    lose(zombie);
                    break;
                }
            }
        }
    }
    
    private void checkWinCondition() {
        if (_playerLost || _gameEnded) return;
        
        // Win condition: Complete all 4 waves and kill all zombies
        if (_currentWave >= 4) {
            // Check if no zombies left on the field
            boolean hasZombies = _gameObjects.stream()
                .anyMatch(obj -> obj instanceof AbstractZombieGameObject);
            
            if (!hasZombies && System.currentTimeMillis() - _gameStartTime > 90000) { // After 90 seconds
                sendClientStatus("WON");
                System.out.println("🎉 You completed all waves! Waiting for server confirmation...");
            }
        }
    }
    
    private void sendClientStatus(String status) {
        if (!networkManager.isConnected()) return;
        
        int zombiesRemaining = (int) _gameObjects.stream()
            .filter(obj -> obj instanceof AbstractZombieGameObject)
            .count();
        
        ClientStatusEvent event = new ClientStatusEvent(
            tick, clientId, _currentWave, zombiesRemaining, 
            !_playerLost, System.currentTimeMillis() - _gameStartTime, status
        );
        
        networkManager.sendEvent(event);
    }
    
    /**
     * Send ready status to server with selected plants
     */
    public void sendReadyStatus(ArrayList<AbstractPlantGameObject.PlantType> selectedPlants) {
        if (!networkManager.isConnected()) return;
        
        this._selectedPlants = selectedPlants;
        
        // Convert PlantType enum to strings for network transmission
        ArrayList<String> plantTypeStrings = new ArrayList<>();
        for (AbstractPlantGameObject.PlantType plantType : selectedPlants) {
            plantTypeStrings.add(plantType.name());
        }
        
        ClientReadyEvent event = new ClientReadyEvent(tick, clientId, plantTypeStrings);
        networkManager.sendEvent(event);
        
        System.out.println("Sent ready status to server with plants: " + plantTypeStrings);
    }
    
    @Override
    public void plantObject(AbstractPlantGameObject object) throws Exception {
        // Handle local planting (this is client-specific)
        super.plantObject(object);
        System.out.println("Planted: " + object.getClass().getSimpleName() + 
                          " at (" + object.getRow() + ", " + object.getColumn() + ")");
    }
    
    @Override
    public void spawnObject(AbstractGameObject object) {
        // Handle local object spawning (bullets, etc.)
        super.spawnObject(object);
    }
    
    @Override
    public void lose(AbstractZombieGameObject winnerZombie) {
        if (!_playerLost) {
            _playerLost = true;
            sendClientStatus("LOST");
            super.lose(winnerZombie);
            System.out.println("💀 You lost! A zombie reached your house.");
        }
    }
    
    @Override
    public void win() {
        if (!_gameEnded) {
            super.win();
            System.out.println("🎉 Victory! You survived all waves!");
        }
    }
    
    public void disconnect() {
        sendClientStatus("DISCONNECTED");
        if (networkManager != null) {
            networkManager.stop();
        }
        System.out.println("Disconnected from server.");
    }
    
    @Override
    public void load() {
        // Clients don't load game state in multiplayer
    }
    
    private void initMap() {
        String objectId = "MAP_" + UUID.randomUUID();
        Coordinate coordinate = new Coordinate(0, 0);
        MapGameObject map = MapGameObject.createMapGameObject(objectId, coordinate, this);
        _currentMap = map;

        for (IEventSubscriber subscriber : _gameObjectSpawnEventSubscribers) {
            subscriber._notify(map);
        }
    }
    
    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }
    
    // Getters for UI
    public boolean isGameStarted() { return _gameStarted; }
    public boolean isGameEnded() { return _gameEnded; }
    public boolean isPlayerLost() { return _playerLost; }
    public int getCurrentWave() { return _currentWave; }
    public String getClientId() { return clientId; }
    public int getZombiesKilled() { return _zombiesKilled; }
    public boolean isConnected() { return networkManager.isConnected(); }
    public String getGameMode() { return _gameMode; }
    public ArrayList<AbstractPlantGameObject.PlantType> getSelectedPlants() { return _selectedPlants; }
}
