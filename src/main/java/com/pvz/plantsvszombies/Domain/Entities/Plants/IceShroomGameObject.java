package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

import java.time.Duration;
import java.util.ArrayList;

public class IceShroomGameObject extends AbstractPlantGameObject {
    public final static Duration EXPLOSION_TIME = Duration.ofSeconds(1);
    public final static Duration FROZEN_TIME = Duration.ofSeconds(4);


    private int _tick;
    private final ArrayList<IEventSubscriber> _explosionEventSubscribers = new ArrayList<>();

    public static IceShroomGameObject createIceShroomGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new IceShroomGameObject(gameEngine, id, coordinate, row, column);
    }

    private IceShroomGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 100;
        this._health = 50;
    }

    public void subscribeToExplosionEvent(IEventSubscriber event) {
        this._explosionEventSubscribers.add(event);
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
        _tick++;
        if (getMilliseconds() == EXPLOSION_TIME.toMillis()) {
            explode();
        }
    }

    @Override
    public void getHit(int damage) {

    }

    private void explode() {
        var allZombies = _gameEngine.queryZombies(z -> true);
        for (AbstractZombieGameObject zombie : allZombies) {
            zombie.getFrozen(FROZEN_TIME);
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
}
