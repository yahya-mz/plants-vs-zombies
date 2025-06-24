package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;

public abstract class AbstractPlantGameObject extends AbstractGameObject {
    protected int _row;
    protected int _column;

    protected int _cost;
    protected int _health;

    public int getHealth() {
        return _health;
    }
    public int getCost() {
        return _cost;
    }
    public int getRow() {
        return _row;
    }
    public int getColumn() {
        return _column;
    }

    public abstract void getHit(int damage);

}
