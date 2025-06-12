package com.pvz.plantsvszombies.Presentation.Common;

public record GridCoordinate(float x, float y) {
    public boolean equals(GridCoordinate coordinate) {
        return (coordinate.x == this.x && coordinate.y == this.y);
    }

    public static double calculateDistance(GridCoordinate c1, GridCoordinate c2) {
        return Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2));
    }
}
