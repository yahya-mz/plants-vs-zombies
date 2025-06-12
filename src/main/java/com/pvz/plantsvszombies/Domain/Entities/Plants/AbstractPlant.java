package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.IGameObject;

public abstract class AbstractPlant implements IGameObject {
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
