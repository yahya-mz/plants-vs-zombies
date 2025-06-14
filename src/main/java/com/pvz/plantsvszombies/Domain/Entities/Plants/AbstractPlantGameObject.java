package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;

public abstract class AbstractPlantGameObject extends AbstractGameObject {
    int _row;
    int _column;

    public abstract double getHealth();
    public abstract double getCost();

    public int getRow() {
        return _row;
    }
    public int getColumn() {
        return _column;
    }
}
