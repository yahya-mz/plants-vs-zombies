package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public interface IGameEngine {

    int getRowsCount();
    int getColumnsCount();

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

    List<AbstractZombieGameObject> queryZombies(Predicate<AbstractZombieGameObject> predicate);
    AbstractZombieGameObject queryZombie(Predicate<AbstractZombieGameObject> predicate);

    List<AbstractGameObject> getGameObjects();
}
