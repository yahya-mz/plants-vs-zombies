package com.pvz.plantsvszombies.Multiplayer.Events;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base class for all shared events that are synchronized between server and clients
 */
public abstract class SharedEvent implements Serializable {
    protected String eventId;
    protected long gameTickWhenCreated;
    
    public SharedEvent() {
        this.eventId = java.util.UUID.randomUUID().toString();
    }
    
    public SharedEvent(long gameTick) {
        this();
        this.gameTickWhenCreated = gameTick;
    }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public long getGameTickWhenCreated() { return gameTickWhenCreated; }
    public void setGameTickWhenCreated(long gameTickWhenCreated) { this.gameTickWhenCreated = gameTickWhenCreated; }
    
    public abstract String getEventType();
}
