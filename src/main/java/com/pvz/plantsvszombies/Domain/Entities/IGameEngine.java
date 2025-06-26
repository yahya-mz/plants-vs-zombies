package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;

import java.util.Comparator;
import java.util.List;

public interface IGameEngine {
    double getPoint();

    void addPoint(double point);

    void subtractPoint(double point);

    void start();

    void update();

    void disposeObject(AbstractGameObject object);

    void spawnObject(AbstractGameObject object);

    void plantObject(AbstractPlantGameObject object) throws Exception;

    AbstractPlantGameObject getPlantAtBlock(int row, int column);
    MapBlock getBlockByCoordinate(Coordinate coordinate);

    List<AbstractGameObject> getGameObjects();
//    void spawnObjectOnMap(AbstractGameObject object, int x, int y);
}
