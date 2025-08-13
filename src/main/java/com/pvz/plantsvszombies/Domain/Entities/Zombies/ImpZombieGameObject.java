package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;

import java.io.Serializable;

public class ImpZombieGameObject extends AbstractZombieGameObject implements Serializable {

    public static ImpZombieGameObject createImpZombieGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new ImpZombieGameObject(gameEngine, id, coordinate, row, column);
    }

    ImpZombieGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._health = 75;
        this._speed = super._speed * 2;

        _zombieType = ZombieType.IMP_ZOMBIE;
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        super.update();
    }

}
