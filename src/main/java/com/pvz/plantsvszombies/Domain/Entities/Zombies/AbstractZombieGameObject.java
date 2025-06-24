package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.AbstractBulletGameObject;

public abstract class AbstractZombieGameObject extends AbstractGameObject {

    protected int _row;
    protected int _column;

    protected double _health;
    protected int _damage;
    protected double _speed;

    public int getRow() {
        return this._row;
    }

    public int getColumn() {
        return this._column;
    }

    public abstract double getHealth();

    public abstract void getHit(AbstractBulletGameObject bullet);

    public abstract double getSpeed();
}
