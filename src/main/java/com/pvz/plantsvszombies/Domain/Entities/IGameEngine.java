package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;

public interface IGameEngine {
    double getPoint();

    void addPoint(double point);

    void subtractPoint(double point);

    void start();

    void update();

    void disposeObject(AbstractGameObject object);

    void spawnObject(AbstractGameObject object);
    void plantObject(AbstractPlantGameObject object) throws Exception;

//    void spawnObjectOnMap(AbstractGameObject object, int x, int y);
}
