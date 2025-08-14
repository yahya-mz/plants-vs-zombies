package com.pvz.plantsvszombies.Multiplayer.Events;

/**
 * Event for client status updates (progress, health, etc.)
 */
public class ClientStatusEvent extends SharedEvent {
    private String clientId;
    private int currentWave;
    private int zombiesRemaining;
    private boolean isAlive;
    private long survivedTime;
    private String status;
    
    public enum ClientStatus {
        CONNECTED,
        PLAYING,
        LOST,
        WON,
        DISCONNECTED
    }
    
    public ClientStatusEvent() {
        super();
    }
    
    public ClientStatusEvent(long gameTick, String clientId, int currentWave, 
                           int zombiesRemaining, boolean isAlive, long survivedTime, String status) {
        super(gameTick);
        this.clientId = clientId;
        this.currentWave = currentWave;
        this.zombiesRemaining = zombiesRemaining;
        this.isAlive = isAlive;
        this.survivedTime = survivedTime;
        this.status = status;
    }
    
    @Override
    public String getEventType() {
        return "client_status";
    }
    
    // Getters and setters
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public int getCurrentWave() { return currentWave; }
    public void setCurrentWave(int currentWave) { this.currentWave = currentWave; }
    
    public int getZombiesRemaining() { return zombiesRemaining; }
    public void setZombiesRemaining(int zombiesRemaining) { this.zombiesRemaining = zombiesRemaining; }
    
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }
    
    public long getSurvivedTime() { return survivedTime; }
    public void setSurvivedTime(long survivedTime) { this.survivedTime = survivedTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
