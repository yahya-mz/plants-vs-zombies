package com.pvz.plantsvszombies.Domain.Entities.Plants;

public abstract class AbstractPlant {
    int row;
    int column;

    double health;

    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }
}
