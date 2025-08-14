package com.pvz.plantsvszombies.Multiplayer.Events;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for all shared events that are synchronized between server and clients
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ZombieSpawnEvent.class, name = "zombie_spawn"),
    @JsonSubTypes.Type(value = SunDropEvent.class, name = "sun_drop"),
    @JsonSubTypes.Type(value = WaveChangeEvent.class, name = "wave_change"),
    @JsonSubTypes.Type(value = GameEndEvent.class, name = "game_end"),
    @JsonSubTypes.Type(value = ClientStatusEvent.class, name = "client_status"),
    @JsonSubTypes.Type(value = GameStartEvent.class, name = "game_start")
})
public abstract class SharedEvent implements Serializable {
    protected String eventId;
    protected LocalDateTime timestamp;
    protected long gameTickWhenCreated;
    
    public SharedEvent() {
        this.timestamp = LocalDateTime.now();
        this.eventId = java.util.UUID.randomUUID().toString();
    }
    
    public SharedEvent(long gameTick) {
        this();
        this.gameTickWhenCreated = gameTick;
    }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public long getGameTickWhenCreated() { return gameTickWhenCreated; }
    public void setGameTickWhenCreated(long gameTickWhenCreated) { this.gameTickWhenCreated = gameTickWhenCreated; }
    
    @com.fasterxml.jackson.annotation.JsonIgnore
    public abstract String getEventType();
}
