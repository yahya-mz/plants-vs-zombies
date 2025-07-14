package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;

public class NormalZombieGameObject extends AbstractZombieGameObject {
    public static NormalZombieGameObject createNormalZombieGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new NormalZombieGameObject(gameEngine, id, coordinate, row, column);
    }

    NormalZombieGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;
        this._speed = this._speed * 1;

        this._health = 125;

    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        super.update();

    }

}
