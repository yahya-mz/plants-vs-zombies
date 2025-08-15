package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;

import java.io.Serial;
import java.io.Serializable;


public class GraveGameObject extends AbstractGameObject implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ویژگی‌هایی که AbstractGameObject نداره
    private int _row;
    private int _column;
    private int _health;

    public static GraveGameObject createGraveGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new GraveGameObject(gameEngine, id, coordinate, row, column);
    }

    private GraveGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;
        this._health = Integer.MAX_VALUE; // عملاً نابود نمیشه
    }

    @Override
    public void spawn() {
        // فعلاً فقط ظاهر میشه
    }

    @Override
    public void update() {
        // قبر رفتار خاصی نداره
    }

    // getter ها
    public int getRow() {
        return _row;
    }

    public int getColumn() {
        return _column;
    }

    public int getHealth() {
        return _health;
    }

    // setter ها
    public void setHealth(int health) {
        this._health = health;
    }
}
