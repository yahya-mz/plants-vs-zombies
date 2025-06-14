package com.pvz.plantsvszombies.Domain.Common;

public class Coordinate {
    private double x;
    private double _y;

    public Coordinate(double x, double y) {
        this.x = x;
        this._y = y;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this._y;
    }

    public boolean equals(Coordinate coordinate) {
        return (coordinate.x == this.x && coordinate._y == this._y);
    }

    public static double calculateDistance(Coordinate c1, Coordinate c2) {
        return Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1._y - c2._y, 2));
    }

    public void traverse(double x, double y) {
        this.x += x;
        this._y += y;
    }

    public Coordinate copy() {
        return new Coordinate(x, _y);
    }
}
