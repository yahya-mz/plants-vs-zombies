package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;


import java.time.Duration;

public class ConeHeadZombieGameObject extends AbstractZombieGameObject {

    public final static Duration BITE_COOL_DOWN = Duration.ofMillis(500);

    public static ConeHeadZombieGameObject createConeHeadZombieGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new ConeHeadZombieGameObject(gameEngine, id, coordinate, row, column);
    }

    ConeHeadZombieGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._health = 175;
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        super.update();
    }

}
