package com.pvz.plantsvszombies.Domain.Common;

import java.io.Serializable;

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

}