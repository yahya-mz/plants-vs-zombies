package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;

public class ImpZombieGameObject extends AbstractGameObject {
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
}
