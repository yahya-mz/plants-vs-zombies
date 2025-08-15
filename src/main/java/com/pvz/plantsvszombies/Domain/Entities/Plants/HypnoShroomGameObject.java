package com.pvz.plantsvszombies.Domain.Entities.Plants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Common.GameMode;
import com.pvz.plantsvszombies.Domain.Engines.NightEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

public class HypnoShroomGameObject extends AbstractPlantGameObject implements Serializable {

    private boolean _isAwake = false;

    private final Duration _coolDown = Duration.ofMillis(3000);
    private int tick = 1;

    private transient ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();
    private transient ArrayList<IEventSubscriber> _wakeUpEventSubscribers = new ArrayList<>();

    public static HypnoShroomGameObject createHypnoShroomGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new HypnoShroomGameObject(gameEngine, id, coordinate, row, column);
    }

    HypnoShroomGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 50;
        this._health = 0;

        this._isAwake = (_gameEngine.getGameMode() == GameMode.NIGHT);
    }

    public void subscribeToEatenEvent(IEventSubscriber event) {
        this._eatenEventSubscribers.add(event);
    }

    public void subscribeToWakeUpEvent(IEventSubscriber event) {
        this._wakeUpEventSubscribers.add(event);
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {

    }

    @Override
    public void getHit(int damage) {
        eaten();
    }

    public boolean isAwake() {
        return _isAwake;
    }

    public void wakeUp() {
        if (!this._isAwake) {
            this._isAwake = true;
            for (IEventSubscriber subscriber : _wakeUpEventSubscribers) {
                subscriber._notify(this);
            }
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
        _eatenEventSubscribers = new ArrayList<>();
        _wakeUpEventSubscribers = new ArrayList<>();
    }
}
