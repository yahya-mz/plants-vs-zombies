package com.pvz.plantsvszombies.Domain.Common;

import java.io.Serializable;

/**
 * Game modes for Plants vs Zombies
 */
public enum GameMode implements Serializable {
    DAY("day"),
    NIGHT("night");
    
    private final String identifier;
    
    GameMode(String identifier) {
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public static GameMode fromString(String mode) {
        if (mode == null) return DAY;
        return mode.toLowerCase().equals("night") ? NIGHT : DAY;
    }
}