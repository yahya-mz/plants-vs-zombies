package com.pvz.plantsvszombies.Domain.Common;

import java.io.Serializable;

/**
 * Enum representing the different game modes available in Plants vs Zombies
 */
public enum GameMode implements Serializable {
    /**
     * Day mode - traditional gameplay with sun dropping from sky, no fog
     */
    DAY("Day Mode", "day"),
    
    /**
     * Night mode - no sun from sky, fog effects, mushroom plants available
     */
    NIGHT("Night Mode", "night");
    
    private final String displayName;
    private final String identifier;
    
    GameMode(String displayName, String identifier) {
        this.displayName = displayName;
        this.identifier = identifier;
    }
    
    /**
     * Get the human-readable display name for the game mode
     * @return Display name suitable for UI
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get the identifier string for the game mode
     * @return String identifier for the mode
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Parse a string to get the corresponding GameMode
     * @param modeString The string to parse (case insensitive)
     * @return The corresponding GameMode, or DAY as default
     */
    public static GameMode fromString(String modeString) {
        if (modeString == null) {
            return DAY;
        }
        
        String normalizedMode = modeString.trim().toLowerCase();
        for (GameMode mode : values()) {
            if (mode.identifier.equals(normalizedMode) || 
                mode.name().toLowerCase().equals(normalizedMode)) {
                return mode;
            }
        }
        
        return DAY; // Default fallback
    }
    
    /**
     * Check if this is a day mode
     * @return true if this is DAY mode
     */
    public boolean isDayMode() {
        return this == DAY;
    }
    
    /**
     * Check if this is a night mode
     * @return true if this is NIGHT mode
     */
    public boolean isNightMode() {
        return this == NIGHT;
    }
}