package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class JalapenoGameObject extends AbstractPlantGameObject {
    private final static Duration EXPLOSION_TIME = Duration.ofMillis(4000);
    private IGameEngine _engine;
    private int tick = 1;
    private boolean isDisposed = false;

    private final ArrayList<IEventSubscriber> _explosionEventSubscribers = new ArrayList<>();

    public static JalapenoGameObject createJalapenoGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new JalapenoGameObject(gameEngine, id, coordinate, row, column);
    }

    private JalapenoGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._engine = gameEngine;
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
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        explode();
                    }
                },
                EXPLOSION_TIME.toMillis()
        );
    }

    @Override
    public void update() {

    }

    @Override
    public void getHit(int damage) {

    }

    private void explode() {
        var zombies = _engine.queryZombies(z -> z.getRow() == _row);
        for (AbstractZombieGameObject zombie:zombies){
            zombie.getBurned();
        }
    }
}
