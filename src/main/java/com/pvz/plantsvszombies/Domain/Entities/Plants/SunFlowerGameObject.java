package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.GlobalSettings;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

public class SunFlowerGameObject extends AbstractPlantGameObject {
    private final Duration _coolDown = Duration.ofMillis(3000);
    private final Duration _timeOut = Duration.ofMillis(20000);
    private IGameEngine _engine;
    private int tick = 1;
    private boolean isDisposed = false;

    private ArrayList<IEventSubscriber> _glowingEventSubscribers = new ArrayList<>();
    private ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();


    public static SunFlowerGameObject createSunFlowerGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new SunFlowerGameObject(gameEngine, id, coordinate, row, column);
    }

    SunFlowerGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._engine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 50;
    }

    public void subscribeToGlowingEvent(IEventSubscriber event) {
        this._glowingEventSubscribers.add(event);
    }

    public void subscribeToEatenEvent(IEventSubscriber event) {
        this._eatenEventSubscribers.add(event);
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        if (!this.isDisposed) {
            tick++;
            if (getMilliseconds() % _coolDown.toMillis() == 0) {
                glow();
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

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

    private void glow(){
        String SunObjectId = "Sun" + UUID.randomUUID();
        var bulletObj = SunGameObject.createSunGameObject(_engine, SunObjectId, this._coordinate.copy());
        _engine.spawnObject(bulletObj);
        for (IEventSubscriber eventSubscriber : _glowingEventSubscribers) {
            eventSubscriber._notify(bulletObj);
        }
    }

    private void eaten() {
        this.isDisposed = true;
        this._engine.disposeObject(this);
        this._engine = null;

        for (IEventSubscriber eventSubscriber : _eatenEventSubscribers) {
            eventSubscriber._notify(this);
        }
    }

}