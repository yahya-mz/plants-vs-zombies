package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.AbstractBulletGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;

import java.io.Serializable;
import java.time.Duration;

public class ScreenDoorZombieGameObject extends AbstractZombieGameObject implements Serializable {

    public final static Duration BITE_COOL_DOWN = Duration.ofMillis(500);

    private enum State {
        MOVING,
        EATING
    }


    public static ScreenDoorZombieGameObject createScreenDoorZombieGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new ScreenDoorZombieGameObject(gameEngine, id, coordinate, row, column);
    }

    ScreenDoorZombieGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._health = 250;
    }

    @Override
    public void spawn() {

    }


    @Override
    public void update() {
        super.update();
    }

    private void checkCollision() {
        for (AbstractGameObject obj : _gameEngine.getGameObjects()) {
            if (obj instanceof AbstractBulletGameObject) {
                if (((AbstractBulletGameObject) obj).getRow() == this._row
                        && Math.abs(((AbstractBulletGameObject) obj).getCoordinate().x() - this._coordinate.x()) < (this._speed + ((AbstractBulletGameObject) obj).getSpeed())) {
                    getHit((AbstractBulletGameObject) obj);
                    ((AbstractBulletGameObject) obj).collide(this);
                }
            }
        }
    }
}
