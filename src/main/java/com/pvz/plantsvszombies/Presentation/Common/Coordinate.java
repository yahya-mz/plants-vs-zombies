package com.pvz.plantsvszombies.Presentation.Common;

public record Coordinate(float x, float y) {
    public boolean equals(Coordinate coordinate) {
        return (coordinate.x == this.x && coordinate.y == this.y);
    }

    public static double calculateDistance(Coordinate c1, Coordinate c2) {
        return Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2));
    }
}
