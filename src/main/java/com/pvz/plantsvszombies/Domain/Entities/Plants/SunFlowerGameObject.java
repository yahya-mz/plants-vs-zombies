package com.pvz.plantsvszombies.Domain.Entities.Plants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

public class SunFlowerGameObject extends AbstractPlantGameObject implements Serializable {
    private final Duration _coolDown = Duration.ofMillis(3000);
    private int tick = 1;

    private transient ArrayList<IEventSubscriber> _glowingEventSubscribers = new ArrayList<>();
    private transient ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();


    public static SunFlowerGameObject createSunFlowerGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new SunFlowerGameObject(gameEngine, id, coordinate, row, column);
    }

    SunFlowerGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
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
        if (!this._isDisposed) {
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

    private void glow(){
        String SunObjectId = "Sun" + UUID.randomUUID();
        var bulletObj = SunGameObject.createSunGameObject(_gameEngine, SunObjectId, this._coordinate.copy());
        _gameEngine.spawnObject(bulletObj);
        for (IEventSubscriber eventSubscriber : _glowingEventSubscribers) {
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

    // Serialization
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _glowingEventSubscribers = new ArrayList<>();
        _eatenEventSubscribers = new ArrayList<>();
    }
}