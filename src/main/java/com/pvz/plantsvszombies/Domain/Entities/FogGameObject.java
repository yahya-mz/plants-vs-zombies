package com.pvz.plantsvszombies.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;

public class FogGameObject extends AbstractGameObject implements Serializable {

    public static Duration FADING_COOL_DOWN = Duration.ofSeconds(10);

    private int _tick;
    private int _column;
    private int _row;

    private boolean _isFadingAway = false;
    private boolean _isFadedAway = false;
    private double _fadingSpeed = 5;
    private double _backingSeed = 5;
    private double _lastFadingMillis = 0;
    private final Coordinate _defualtCoordinate;

    @JsonIgnore
    public transient ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();


    public static FogGameObject createFogGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new FogGameObject(gameEngine, id, coordinate, row, column);
    }

    private FogGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._defualtCoordinate = coordinate.copy();
        this._coordinate = coordinate.copy();
        this._row = row;
        this._column = column;
    }

    public int getColumn() {
        return _column;
    }

    public int getRow() {
        return _row;
    }

    public double getHomeX()        { return _defualtCoordinate.x(); } // (defualt → default)

    public double getOutProgress() {
        double denom = Math.max(1.0, _gameEngine.getWindowWidth() - _defualtCoordinate.x());
        return Math.max(0.0, Math.min(1.0, (_coordinate.x() - _defualtCoordinate.x()) / denom));
    }
    public double getBackProgress() {
        return 1.0 - getOutProgress();
    }

    public void subscribeToMovementEvent(IEventSubscriber eventSubscriber) {
        _movementEventSubscribers.add(eventSubscriber);
    }

    public void fadeAwayCompletely() {
        if (!_isFadedAway) {
            _isFadingAway = true;
            _lastFadingMillis = getMilliseconds();
        }
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        if (_isFadedAway &&
                (getMilliseconds() - _lastFadingMillis) >= FADING_COOL_DOWN.toMillis()) {
            _coordinate.traverse(-(_backingSeed), 0);
            if (Math.abs(_coordinate.x() - _defualtCoordinate.x()) < _backingSeed) {
                _coordinate = _defualtCoordinate.copy();
                _isFadedAway = false;
            }
            for (IEventSubscriber eventSubscriber : _movementEventSubscribers) {
                eventSubscriber._notify(this);
            }
        }
        if (_isFadingAway) {
            fadeABit();
            if (_coordinate.x() >= _gameEngine.getWindowWidth()) {
                _isFadingAway = false;
                _isFadedAway = true;
                _coordinate = new Coordinate(_gameEngine.getWindowWidth(), _coordinate.y());
            }
        }
        _tick++;
    }

    private void fadeABit() {
        _coordinate.traverse(_fadingSpeed, 0);
        for (IEventSubscriber eventSubscriber : _movementEventSubscribers) {
            eventSubscriber._notify(this);
        }
    }

    private double getMilliseconds() {
        return this._tick * 1000.0 / GlobalSettings.FPS;
    }

    // Serialization
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _movementEventSubscribers = new ArrayList<>();
    }
}
