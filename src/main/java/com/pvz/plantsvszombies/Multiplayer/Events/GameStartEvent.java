package com.pvz.plantsvszombies.Multiplayer.Events;

/**
 * Event sent when enough players have joined and the game is starting
 */
public class GameStartEvent extends SharedEvent {
    private int playerCount;
    
    public GameStartEvent() {
        super();
    }
    
    public GameStartEvent(long gameTick, int playerCount) {
        super(gameTick);
        this.playerCount = playerCount;
    }
    
    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getEventType() {
        return "game_start";
    }
    
    // Getters and setters
    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
}

