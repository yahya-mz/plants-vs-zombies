package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;
import com.pvz.plantsvszombies.GlobalSettings;

import java.util.ArrayList;
import java.util.UUID;

public class PeashooterGameObject extends AbstractPlantGameObject {
    private final String _ID;
    private Coordinate _coordinate;

    private final double _health = 50;
    private final double _cost = 100;

    private IGameEngine _engine;

    private int tick = 1;

    private boolean isDisposed = false;

    private ArrayList<IEventSubscriber> _shootingEventSubscribers = new ArrayList<>();
    private ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();


    public static PeashooterGameObject createPeashooterGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new PeashooterGameObject(gameEngine, id, coordinate, row, column);
    }

    PeashooterGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._engine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;
    }

    private void shoot() {
        String BulletObjectId = "NormalBullet" + UUID.randomUUID();
        var bulletObj = NormalBulletGameObject.createNormalBulletGameObject(_engine, BulletObjectId, this._coordinate.copy());
        _engine.spawnObject(bulletObj);
        for (IEventSubscriber eventSubscriber : _shootingEventSubscribers) {
            eventSubscriber._notify(bulletObj);
        }
    }

    private void eaten() {
        for (IEventSubscriber eventSubscriber : _eatenEventSubscribers) {
            eventSubscriber._notify(this);
        }
    }

    public void subscribeToShootingEvent(IEventSubscriber event) {
        this._shootingEventSubscribers.add(event);
    }

    public void subscribeToEatenEvent(IEventSubscriber event) {
        this._eatenEventSubscribers.add(event);
    }


    @Override
    public String getId() {
        return this._ID;
    }

    @Override
    public Coordinate getCoordinate() {
        return this._coordinate;
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        tick++;
        if (getMilliseconds() % 4000 == 0) {
            shoot();
        }
    }

    @Override
    public double getHealth() {
        return this._health;
    }

    @Override
    public double getCost() {
        return this._cost;
    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

    public void setCoordinate(Coordinate coordinate) {
        this._coordinate = coordinate;
    }


}
