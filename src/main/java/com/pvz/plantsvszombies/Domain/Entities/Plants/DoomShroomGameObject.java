package com.pvz.plantsvszombies.Domain.Entities.Plants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Common.GameMode;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

public class DoomShroomGameObject extends AbstractPlantGameObject implements Serializable {
    public final static Duration EXPLOSION_TIME = Duration.ofSeconds(1);

    private int _tick;
    private boolean _isAwake;
    private transient ArrayList<IEventSubscriber> _explosionEventSubscribers = new ArrayList<>();
    private transient ArrayList<IEventSubscriber> _wakeUpEventSubscribers = new ArrayList<>();

    public static DoomShroomGameObject createCherryBombGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new DoomShroomGameObject(gameEngine, id, coordinate, row, column);
    }

    private DoomShroomGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 100;
        this._health = 50;
        
        // Set initial awake state based on game mode
        GameMode gameMode = _gameEngine.getGameMode();
        this._isAwake = (gameMode == GameMode.NIGHT);
    }

    public void subscribeToExplosionEvent(IEventSubscriber event) {
        this._explosionEventSubscribers.add(event);
    }

    public void subscribeToWakeUpEvent(IEventSubscriber event) {
        this._wakeUpEventSubscribers.add(event);
    }

    @Override
    public void spawn() {
//        new Timer().schedule(
//                new TimerTask() {
//                    @Override
//                    public void run() {
//                        explode();
//                    }
//                },
//                EXPLOSION_TIME.toMillis()
//        );
    }

    @Override
    public void update() {
        if (_isAwake) {
            _tick++;
            if (getMilliseconds() == EXPLOSION_TIME.toMillis()) {
                explode();
            }
        }
    }

    @Override
    public void getHit(int damage) {

    }

    private void explode() {
        var allZombies = _gameEngine.queryZombies(z -> true);
        for (AbstractZombieGameObject zombie : allZombies) {
            zombie.getBurned();
        }
        // The Game Engine will subscribe to this event
        for (IEventSubscriber _subscriber : _explosionEventSubscribers) {
            _subscriber._notify(this);
        }
        super.dispose();
    }

    private double getMilliseconds() {
        return this._tick * 1000.0 / GlobalSettings.FPS;
    }

    public boolean isAwake() {
        return this._isAwake;
    }

    public void wakeUp() {
        if (!this._isAwake) {
            this._isAwake = true;
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
        _explosionEventSubscribers = new ArrayList<>();
        _wakeUpEventSubscribers = new ArrayList<>();
    }
}
