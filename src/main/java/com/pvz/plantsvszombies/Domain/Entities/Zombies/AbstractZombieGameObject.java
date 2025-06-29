package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.AbstractBulletGameObject;

public abstract class AbstractZombieGameObject extends AbstractGameObject {

    public enum ZombieType{
        NORMAL_ZOMBIE,
        CONE_HEAD_ZOMBIE,
        SCREEN_DOOR_ZOMBIE,
        IMP_ZOMBIE
    }

    protected int _row;
    protected int _column;

    protected double _health;
    protected int _damage = 25;
    protected double _speed;

    public int getRow() {
        return this._row;
    }

    public int getColumn() {
        return this._column;
    }

    public double getHealth() {
        return this._health;
    }

    public abstract void getHit(AbstractBulletGameObject bullet);

    public abstract void getBurned();

    public abstract double getSpeed();
}
