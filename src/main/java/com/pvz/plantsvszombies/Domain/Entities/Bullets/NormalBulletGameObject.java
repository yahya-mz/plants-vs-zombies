package com.pvz.plantsvszombies.Domain.Entities.Bullets;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;

import java.util.ArrayList;

public class NormalBulletGameObject extends AbstractGameObject {


    private final double _damage = 50;
    private final double _speed = 5;

    private final IGameEngine _engine;

    private boolean isDisposed = false;

    private final ArrayList<IEventSubscriber> _collisionEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();

    public static NormalBulletGameObject createNormalBulletGameObject(IGameEngine gameEngine, String id, Coordinate coordinate) {
        return new NormalBulletGameObject(gameEngine, id, coordinate);
    }

    NormalBulletGameObject(IGameEngine gameEngine, String id, Coordinate coordinate) {
        this._engine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        this._coordinate.traverse(_speed, 0);
        for (IEventSubscriber subscriber : _movementEventSubscribers) {
            subscriber._notify(this);
        }
    }

    public void dispose() {
        _engine.disposeObject(this);
    }

    public void subscribeToCollisionEvent(IEventSubscriber subscriber) {
        _collisionEventSubscribers.add(subscriber);
    }

    public void subscribeToMovementEvent(IEventSubscriber subscriber) {
        _movementEventSubscribers.add(subscriber);
    }

    private void collide() {
        if (!isDisposed) {
            for (IEventSubscriber subscriber : _collisionEventSubscribers) {
                subscriber._notify(this);
                isDisposed = true;
                _engine.disposeObject(this);
            }
        }
    }
}
