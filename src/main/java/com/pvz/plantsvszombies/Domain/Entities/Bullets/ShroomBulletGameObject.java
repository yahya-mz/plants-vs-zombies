package com.pvz.plantsvszombies.Domain.Entities.Bullets;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;

import java.io.Serializable;
import java.util.ArrayList;

public class ShroomBulletGameObject extends AbstractBulletGameObject implements Serializable {

    private transient final ArrayList<IEventSubscriber> _collisionEventSubscribers = new ArrayList<>();
    private transient final ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();

    public static ShroomBulletGameObject createShroomBulletGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row) {
        return new ShroomBulletGameObject(gameEngine, id, coordinate, row);
    }

    ShroomBulletGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._bulletType = AbstractBulletGameObject.BulletType.NORMAL_BULLET;

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
                subscriber._notify(this);
            }
            super.dispose();
        }
    }

    public void subscribeToCollisionEvent(IEventSubscriber eventSubscriber) {
        _collisionEventSubscribers.add(eventSubscriber);
    }

    public void subscribeToMovementEvent(IEventSubscriber eventSubscriber) {
        _movementEventSubscribers.add(eventSubscriber);
    }
}
