package com.pvz.plantsvszombies.Multiplayer.Events;

/**
 * Event for game end conditions and winner announcement
 */
public class GameEndEvent extends SharedEvent {
    private String winnerId;
    private String winnerName;
    private EndReason endReason;
    private long completionTime;
    
    public enum EndReason {
        WAVES_COMPLETED,    // Someone completed all waves first
        PLAYER_LOST,       // All players lost to zombies
        LAST_SURVIVOR,     // Only one player left alive
        DISCONNECTION,     // Connection lost
        SERVER_SHUTDOWN    // Server stopped
    }
    
    public GameEndEvent() {
        super();
    }
    
    public GameEndEvent(long gameTick, String winnerId, String winnerName, 
                       EndReason endReason, long completionTime) {
        super(gameTick);
        this.winnerId = winnerId;
        this.winnerName = winnerName;
        this.endReason = endReason;
        this.completionTime = completionTime;
    }
    
    @Override
    public String getEventType() {
        return "game_end";
    }
    
    // Getters and setters
    public String getWinnerId() { return winnerId; }
    public void setWinnerId(String winnerId) { this.winnerId = winnerId; }
    
    public String getWinnerName() { return winnerName; }
    public void setWinnerName(String winnerName) { this.winnerName = winnerName; }
    
    public EndReason getEndReason() { return endReason; }
    public void setEndReason(EndReason endReason) { this.endReason = endReason; }
    
    public long getCompletionTime() { return completionTime; }
    public void setCompletionTime(long completionTime) { this.completionTime = completionTime; }
}
