package com.pvz.plantsvszombies.Domain.Common;

import java.io.Serializable;

public class Coordinate implements Serializable {
    private double _x;
    private double _y;

    public Coordinate(double x, double y) {
        this._x = x;
        this._y = y;
    }

    public double x() {
        return this._x;
    }

    public double y() {
        return this._y;
    }

    public boolean equals(Coordinate coordinate) {
        return (coordinate._x == this._x && coordinate._y == this._y);
    }

    public static double calculateDistance(Coordinate c1, Coordinate c2) {
        return Math.sqrt(Math.pow(c1._x - c2._x, 2) + Math.pow(c1._y - c2._y, 2));
    }

    public void traverse(double x, double y) {
        this._x += x;
        this._y += y;
    }

    public Coordinate copy() {
        return new Coordinate(_x, _y);
    }
}
