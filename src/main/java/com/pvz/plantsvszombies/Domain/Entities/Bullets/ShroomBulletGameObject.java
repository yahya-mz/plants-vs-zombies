package com.pvz.plantsvszombies.Domain.Entities.Bullets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;

public class ShroomBulletGameObject extends AbstractBulletGameObject implements Serializable {

    private transient ArrayList<IEventSubscriber> _collisionEventSubscribers = new ArrayList<>();
    private transient ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();

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

    // Serialization
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _movementEventSubscribers = new ArrayList<>();
        _collisionEventSubscribers = new ArrayList<>();
    }
}
