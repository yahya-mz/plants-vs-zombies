package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;

public abstract class AbstractPlantGameObject extends AbstractGameObject {
    public enum PlantType{
        PEASHOOTER,
        REPEATER,
        CHERRY_BOMB,
        JALAPENO,
        WALL_NUT,
        TALL_NUT,
        SUNFLOWER,
        SNOW_PEA,
        SCAREDY_SHROOM,
        PUFF_SHROOM,
        ICE_SHROOM,
        HYPNO_SHROOM,
        BLOVER,
        COFFEE_BEAN

    }

    protected int _row;
    protected int _column;

    protected int _cost;
    protected int _health = 100;

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
