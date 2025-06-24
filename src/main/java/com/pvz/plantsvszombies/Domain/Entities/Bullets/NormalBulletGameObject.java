package com.pvz.plantsvszombies.Domain.Entities.Bullets;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;

import java.util.ArrayList;

public class NormalBulletGameObject extends AbstractBulletGameObject {
    private final IGameEngine _engine;

    private boolean _isDisposed = false;

    private final ArrayList<IEventSubscriber> _collisionEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();

    public static NormalBulletGameObject createNormalBulletGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row) {
        return new NormalBulletGameObject(gameEngine, id, coordinate,row);
    }

    NormalBulletGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row) {
        this._engine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._bulletType = BulletType.NORMAL_BULLET;

        this._speed = 5;
        this._damage = 50;
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        if (!_isDisposed){
            this._coordinate.traverse(_speed, 0);
            for (IEventSubscriber subscriber : _movementEventSubscribers) {
                subscriber._notify(this);
            }
        }
    }
    @Override
    public void collide() {
        if (!_isDisposed) {
            for (IEventSubscriber subscriber : _collisionEventSubscribers) {
                subscriber._notify(this);
                _isDisposed = true;
                _engine.disposeObject(this);
            }
        }
    }

    public void subscribeToCollisionEvent(IEventSubscriber subscriber) {
        _collisionEventSubscribers.add(subscriber);
    }

    public void subscribeToMovementEvent(IEventSubscriber subscriber) {
        _movementEventSubscribers.add(subscriber);
    }

}
