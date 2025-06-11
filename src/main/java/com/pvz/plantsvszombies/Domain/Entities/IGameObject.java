package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;

public interface IGameObject {

    String getId();
    Coordinate getCoordinate();

    void spawn();
    void update();
}
