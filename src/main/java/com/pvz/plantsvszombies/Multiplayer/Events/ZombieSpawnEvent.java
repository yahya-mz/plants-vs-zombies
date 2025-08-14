package com.pvz.plantsvszombies.Multiplayer.Events;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;

/**
 * Event for synchronized zombie spawning across all clients
 */
public class ZombieSpawnEvent extends SharedEvent {
    private int row;
    private int column;
    private AbstractZombieGameObject.ZombieType zombieType;
    private String zombieId;
    private Coordinate spawnCoordinate;
    
    public ZombieSpawnEvent() {
        super();
    }
    
    public ZombieSpawnEvent(long gameTick, int row, int column, 
                           AbstractZombieGameObject.ZombieType zombieType, 
                           String zombieId, Coordinate coordinate) {
        super(gameTick);
        this.row = row;
        this.column = column;
        this.zombieType = zombieType;
        this.zombieId = zombieId;
        this.spawnCoordinate = coordinate;
    }
    
    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getEventType() {
        return "zombie_spawn";
    }
    
    // Getters and setters
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    
    public int getColumn() { return column; }
    public void setColumn(int column) { this.column = column; }
    
    public AbstractZombieGameObject.ZombieType getZombieType() { return zombieType; }
    public void setZombieType(AbstractZombieGameObject.ZombieType zombieType) { this.zombieType = zombieType; }
    
    public String getZombieId() { return zombieId; }
    public void setZombieId(String zombieId) { this.zombieId = zombieId; }
    
    public Coordinate getSpawnCoordinate() { return spawnCoordinate; }
    public void setSpawnCoordinate(Coordinate spawnCoordinate) { this.spawnCoordinate = spawnCoordinate; }
}
