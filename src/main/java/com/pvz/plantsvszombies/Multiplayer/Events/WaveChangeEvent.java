package com.pvz.plantsvszombies.Multiplayer.Events;

/**
 * Event for synchronized wave changes across all clients
 */
public class WaveChangeEvent extends SharedEvent {
    private int newWave;
    private long waveStartTime;
    
    public WaveChangeEvent() {
        super();
    }
    
    public WaveChangeEvent(long gameTick, int newWave, long waveStartTime) {
        super(gameTick);
        this.newWave = newWave;
        this.waveStartTime = waveStartTime;
    }
    
    @Override
    public String getEventType() {
        return "wave_change";
    }
    
    // Getters and setters
    public int getNewWave() { return newWave; }
    public void setNewWave(int newWave) { this.newWave = newWave; }
    
    public long getWaveStartTime() { return waveStartTime; }
    public void setWaveStartTime(long waveStartTime) { this.waveStartTime = waveStartTime; }
}
