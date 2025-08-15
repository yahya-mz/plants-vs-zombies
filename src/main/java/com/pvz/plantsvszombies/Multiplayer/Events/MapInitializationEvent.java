package com.pvz.plantsvszombies.Multiplayer.Events;

import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;

/**
 * Event for initializing the game map on clients
 */
public class MapInitializationEvent extends SharedEvent {
    private String mapId;
    private double mapX;
    private double mapY;
    private int rows;
    private int columns;
    private String gameMode; // "day" or "night"

    public MapInitializationEvent() {
        super();
    }

    public MapInitializationEvent(long gameTick, String mapId, double mapX, double mapY, int rows, int columns, String gameMode) {
        super(gameTick);
        this.mapId = mapId;
        this.mapX = mapX;
        this.mapY = mapY;
        this.rows = rows;
        this.columns = columns;
        this.gameMode = gameMode;
    }

    @Override
    public String getEventType() {
        return "map_initialization";
    }

    // Getters and setters
    public String getMapId() { return mapId; }
    public void setMapId(String mapId) { this.mapId = mapId; }

    public double getMapX() { return mapX; }
    public void setMapX(double mapX) { this.mapX = mapX; }

    public double getMapY() { return mapY; }
    public void setMapY(double mapY) { this.mapY = mapY; }

    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }

    public int getColumns() { return columns; }
    public void setColumns(int columns) { this.columns = columns; }

    public String getGameMode() { return gameMode; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }
}