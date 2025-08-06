package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Engines.DayEngine;
import com.pvz.plantsvszombies.Domain.Engines.NightEngine;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

public class HypnoShroomGameObject extends AbstractPlantGameObject implements Serializable {

    private boolean _isAwake = false;

    private final Duration _coolDown = Duration.ofMillis(3000);
    private int tick = 1;

    private transient final ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();

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

        _isAwake = _gameEngine instanceof NightEngine;
    }

    public void subscribeToEatenEvent(IEventSubscriber event) {
        this._eatenEventSubscribers.add(event);
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
