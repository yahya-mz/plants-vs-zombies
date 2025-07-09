package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Interfaces.IGameEngine;
import com.pvz.plantsvszombies.GlobalSettings;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

public class PeashooterGameObject extends AbstractPlantGameObject {
    private final Duration _coolDown = Duration.ofMillis(4000);
    private int tick = 1;

    private final ArrayList<IEventSubscriber> _shootingEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();

    public static PeashooterGameObject createPeashooterGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new PeashooterGameObject(gameEngine, id, coordinate, row, column);
    }

    private PeashooterGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 100;
    }

    public void subscribeToShootingEvent(IEventSubscriber event) {
        this._shootingEventSubscribers.add(event);
    }

    public void subscribeToEatenEvent(IEventSubscriber event) {
        this._eatenEventSubscribers.add(event);
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        if (!this._isDisposed) {
            tick++;
            if (getMilliseconds() % _coolDown.toMillis() == 0) {
                shoot();
            }
        }
    }

    @Override
    public void getHit(int damage) {
        _health -= damage;
        if (_health <= 0) {
            eaten();
        }

    }

    private void shoot() {
        String BulletObjectId = "NormalBullet" + UUID.randomUUID();
        var bulletObj = NormalBulletGameObject.createNormalBulletGameObject(_gameEngine, BulletObjectId, new Coordinate(this._coordinate.x() + 30, this._coordinate.y() - 20), getRow());
        _gameEngine.spawnObject(bulletObj);
        for (IEventSubscriber eventSubscriber : _shootingEventSubscribers) {
            eventSubscriber._notify(bulletObj);
        }
    }

    private void eaten() {
        for (IEventSubscriber eventSubscriber : _eatenEventSubscribers) {
            eventSubscriber._notify(this);
        }
        super.dispose();
    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }
}
