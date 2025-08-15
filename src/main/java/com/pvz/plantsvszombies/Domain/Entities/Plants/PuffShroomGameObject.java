package com.pvz.plantsvszombies.Domain.Entities.Plants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Common.GameMode;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.ShroomBulletGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Domain.Engines.NightEngine;

public class PuffShroomGameObject extends AbstractPlantGameObject implements Serializable {
    public enum PuffshroomState {
        STANDING,
        SLEEPING
    }

    private PuffshroomState _visualState = PuffshroomState.STANDING;
    private final Duration _coolDown = Duration.ofMillis(3000);
    private int tick = 1;
    private int _lastShootTick = 0;
    private boolean _isAwake;

    private transient ArrayList<IEventSubscriber> _shootingEventSubscribers = new ArrayList<>();
    private transient ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();
    private transient ArrayList<IEventSubscriber> _wakeUpEventSubscribers = new ArrayList<>();

    public static PuffShroomGameObject createPuffShroomGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new PuffShroomGameObject(gameEngine, id, coordinate, row, column);
    }

    PuffShroomGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 0;
        this._health = 100;
        
        // Set initial awake state and visual state based on game mode
        GameMode gameMode = _gameEngine.getGameMode();
        this._isAwake = (gameMode == GameMode.NIGHT);
        
        if (this._isAwake) {
            this._visualState = PuffshroomState.STANDING;
        } else {
            this._visualState = PuffshroomState.SLEEPING;
        }
    }

    public PuffshroomState getVisualState() {
        return _visualState;
    }

    public void setVisualState(PuffshroomState state) {
        _visualState = state;
    }

    public void subscribeToShootingEvent(IEventSubscriber event) {
        this._shootingEventSubscribers.add(event);
    }

    public void subscribeToEatenEvent(IEventSubscriber event) {
        this._eatenEventSubscribers.add(event);
    }

    public void subscribeToWakeUpEvent(IEventSubscriber event) {
        this._wakeUpEventSubscribers.add(event);
    }

    @Override
    public void spawn() {
        if (_gameEngine instanceof NightEngine) {
            _visualState = PuffshroomState.STANDING;
        } else {
            _visualState = PuffshroomState.SLEEPING;
        }
    }

    @Override
    public void update() {
        if (!this._isDisposed && _isAwake && !_visualState.equals(PuffshroomState.SLEEPING)) {
            tick++;
            var rowsZombies = _gameEngine.getZombiesByRow(_row);
            if (!rowsZombies.isEmpty()) {
                var frontZombie = rowsZombies.getFirst(); // Most front zombie
                if (frontZombie.getColumn() - _column < 4) {
                    if ((_lastShootTick * 1000 / GlobalSettings.FPS) % _coolDown.toMillis() == 0) {
                        shoot();
                        _lastShootTick = 0;
                    }
                    _lastShootTick++;
                }
            } else {
                _lastShootTick = 0;
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

    private void eaten() {
        for (IEventSubscriber eventSubscriber : _eatenEventSubscribers) {
            eventSubscriber._notify(this);
        }
        super.dispose();
    }

    private void shoot() {
        String BulletObjectId = "ShroomBullet" + UUID.randomUUID();
        var bulletObj = ShroomBulletGameObject.createShroomBulletGameObject(_gameEngine, BulletObjectId, new Coordinate(this._coordinate.x() + 30, this._coordinate.y() - 20), getRow());
        _gameEngine.spawnObject(bulletObj);
        for (IEventSubscriber eventSubscriber : _shootingEventSubscribers) {
            eventSubscriber._notify(bulletObj);
        }
    }

    public PuffshroomState getState() {
        return this._visualState;
    }

    public void setState(PuffshroomState state) {
        this._visualState = state;
    }

    public boolean isAwake() {
        return this._isAwake;
    }

    public void wakeUp() {
        if (!this._isAwake) {
            this._isAwake = true;
            this._visualState = PuffshroomState.STANDING;
            for (IEventSubscriber subscriber : _wakeUpEventSubscribers) {
                subscriber._notify(this);
            }
        }
    }

    // Serialization
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _shootingEventSubscribers = new ArrayList<>();
        _eatenEventSubscribers = new ArrayList<>();
        _wakeUpEventSubscribers = new ArrayList<>();
    }
}
