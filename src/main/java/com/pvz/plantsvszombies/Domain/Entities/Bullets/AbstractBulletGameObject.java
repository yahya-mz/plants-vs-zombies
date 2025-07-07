package com.pvz.plantsvszombies.Domain.Entities.Bullets;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;

public abstract class AbstractBulletGameObject extends AbstractGameObject {
    public enum BulletType {
        NORMAL_BULLET,
        ICED_BULLET
    }

    protected BulletType _bulletType;
    protected double _damage;
    protected double _speed;
    protected int _row;

    public double getDamage() {
        return _damage;
    }
    public double getSpeed() {
        return _speed;
    }
    public double getRow() {
        return _row;
    }
    public BulletType getBulletType() {
        return _bulletType;
    }

    public abstract void collide();
}
