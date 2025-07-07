package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CherryBombGameObject extends AbstractPlantGameObject {
    private final static Duration EXPLOSION_TIME = Duration.ofMillis(4000);
    private IGameEngine _engine;
    private int tick = 1;
    private boolean isDisposed = false;

    private final ArrayList<IEventSubscriber> _explosionEventSubscribers = new ArrayList<>();

    public static CherryBombGameObject createCherryBombGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new CherryBombGameObject(gameEngine, id, coordinate, row, column);
    }

    private CherryBombGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
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
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                final int final_i = i;
                final int final_j = j;
                var zombie = _engine.queryZombie(z -> z.getRow() == _row + final_i && z.getColumn() == _column + final_j);
                if (zombie != null) {
                    zombie.getBurned();
                }
            }
        }
    }
}
