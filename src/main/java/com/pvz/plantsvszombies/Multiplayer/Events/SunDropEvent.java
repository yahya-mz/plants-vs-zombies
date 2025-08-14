package com.pvz.plantsvszombies.Multiplayer.Events;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;

/**
 * Event for synchronized sun dropping across all clients
 */
public class SunDropEvent extends SharedEvent {
    private String sunId;
    private Coordinate dropCoordinate;
    private int sunValue;
    
    public SunDropEvent() {
        super();
    }
    
    public SunDropEvent(long gameTick, String sunId, Coordinate coordinate, int sunValue) {
        super(gameTick);
        this.sunId = sunId;
        this.dropCoordinate = coordinate;
        this.sunValue = sunValue;
    }
    
    @Override
    public String getEventType() {
        return "sun_drop";
    }
    
    // Getters and setters
    public String getSunId() { return sunId; }
    public void setSunId(String sunId) { this.sunId = sunId; }
    
    public Coordinate getDropCoordinate() { return dropCoordinate; }
    public void setDropCoordinate(Coordinate dropCoordinate) { this.dropCoordinate = dropCoordinate; }
    
    public int getSunValue() { return sunValue; }
    public void setSunValue(int sunValue) { this.sunValue = sunValue; }
}
