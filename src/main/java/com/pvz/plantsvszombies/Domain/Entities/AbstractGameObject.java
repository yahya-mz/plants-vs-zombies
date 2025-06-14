package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;

public abstract class AbstractGameObject {

    protected String _ID;
    protected Coordinate _coordinate;

    public String getId() {
        return this._ID;
    }

    public Coordinate getCoordinate() {
        return this._coordinate;
    }
    public abstract void spawn();

    public abstract void update();
}
