package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;

public class WallNutGameObject extends AbstractPlantGameObject {
    @Override
    public String getId() {
        return "";
    }

    @Override
    public Coordinate getCoordinate() {
        return null;
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {

    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public double getCost() {
        return 0;
    }
}
