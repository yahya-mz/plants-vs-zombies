package com.pvz.plantsvszombies.Domain.Entities.Plants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Engines.NightEngine;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.ShroomBulletGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

public class ScaredyShroomGameObject extends AbstractPlantGameObject implements Serializable {

    public enum ScaredyShroomState {
        CRYING,
        SLEEPING,
        STANDING
    }

    private final Duration _coolDown = Duration.ofMillis(4000);
    private int tick = 1;

    public ScaredyShroomState _state;

    private transient ArrayList<IEventSubscriber> _shootingEventSubscribers = new ArrayList<>();
    private transient ArrayList<IEventSubscriber> _switchAwakenessEventSubscribers = new ArrayList<>();
    private transient ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();

    public static ScaredyShroomGameObject createScaredyShroomGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new ScaredyShroomGameObject(gameEngine, id, coordinate, row, column);
    }

    private ScaredyShroomGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 0;
    }

    public void subscribeToShootingEvent(IEventSubscriber event) {
        this._shootingEventSubscribers.add(event);
    }

    public void subscribeToEatenEvent(IEventSubscriber event) {
        this._eatenEventSubscribers.add(event);
    }

    public void subscribeToSwitchAwakenessEvent(IEventSubscriber event) {
        this._switchAwakenessEventSubscribers.add(event);
    }

    @Override
    public void spawn() {
        if (_gameEngine instanceof NightEngine) {
            _state = ScaredyShroomState.STANDING;
        } else {
            _state = ScaredyShroomState.SLEEPING;
        }
    }

    private double _lastShootTick = 0;

    @Override
    public void update() {
        if (!this._isDisposed && !_state.equals(ScaredyShroomState.SLEEPING)) {
            tick++;
            var rowsZombies = _gameEngine.getZombiesByRow(_row);
            if (!rowsZombies.isEmpty()) {
                var frontZombie = rowsZombies.getFirst(); // Most front zombie
                if (frontZombie.getColumn() - _column < 2) {
                    _state = ScaredyShroomState.CRYING;
                    for (IEventSubscriber eventSubscriber : _switchAwakenessEventSubscribers) {
                        eventSubscriber._notify(this);
                    }
                    _lastShootTick = 0;
                    return;
                }
                if ((_lastShootTick * 1000 / GlobalSettings.FPS) % _coolDown.toMillis() == 0
                        && frontZombie.getColumn() - 4 <= _column) {
                    shoot();
                    _lastShootTick = 0;
                }
                _lastShootTick++;
            } else {
                _lastShootTick = 0;
            }

            _state = ScaredyShroomState.STANDING;
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
        String BulletObjectId = "ShroomBullet" + UUID.randomUUID();
        var bulletObj = ShroomBulletGameObject.createShroomBulletGameObject(_gameEngine, BulletObjectId, new Coordinate(this._coordinate.x() + 30, this._coordinate.y() - 20), getRow());
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

    // Serialization
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _shootingEventSubscribers = new ArrayList<>();
        _switchAwakenessEventSubscribers = new ArrayList<>();
        _eatenEventSubscribers = new ArrayList<>();
    }
}
