package com.pvz.plantsvszombies.Domain.Entities.Bullets;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Interfaces.IGameEngine;

import java.util.ArrayList;

public class SnowBulletGameObject extends AbstractBulletGameObject {
    private final ArrayList<IEventSubscriber> _collisionEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();

    public static SnowBulletGameObject createSnowBulletGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row) {
        return new SnowBulletGameObject(gameEngine, id, coordinate, row);
    }

    SnowBulletGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._bulletType = BulletType.SNOW_BULLET;

        this._speed = 7;
        this._damage = 25;
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        if (!_isDisposed) {
            this._coordinate.traverse(_speed, 0);
            for (IEventSubscriber subscriber : _movementEventSubscribers) {
                subscriber._notify(this);
            }
        }
    }

    @Override
    public void collide(AbstractGameObject collidedWith) {
        if (!_isDisposed) {
            for (IEventSubscriber subscriber : _collisionEventSubscribers) {
                subscriber._notify(collidedWith);
            }
            super.dispose();
        }
    }

    public void subscribeToCollisionEvent(IEventSubscriber subscriber) {
        _collisionEventSubscribers.add(subscriber);
    }

    public void subscribeToMovementEvent(IEventSubscriber subscriber) {
        _movementEventSubscribers.add(subscriber);
    }
}
