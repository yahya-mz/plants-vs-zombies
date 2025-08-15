package com.pvz.plantsvszombies.Multiplayer.Events;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;

import java.util.List;

/**
 * Event for initializing the visual engine on clients with game objects
 */
public class VisualEngineInitializationEvent extends SharedEvent {
    private List<AbstractGameObject> gameObjects;
    private String gameMode; // "day" or "night"
    private boolean includeMap; // whether to include map initialization

    public VisualEngineInitializationEvent() {
        super();
    }

    public VisualEngineInitializationEvent(long gameTick, List<AbstractGameObject> gameObjects, String gameMode, boolean includeMap) {
        super(gameTick);
        this.gameObjects = gameObjects;
        this.gameMode = gameMode;
        this.includeMap = includeMap;
    }

    @Override
    public String getEventType() {
        return "visual_engine_initialization";
    }

    // Getters and setters
    public List<AbstractGameObject> getGameObjects() { return gameObjects; }
    public void setGameObjects(List<AbstractGameObject> gameObjects) { this.gameObjects = gameObjects; }

    public String getGameMode() { return gameMode; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }

    public boolean isIncludeMap() { return includeMap; }
    public void setIncludeMap(boolean includeMap) { this.includeMap = includeMap; }
}