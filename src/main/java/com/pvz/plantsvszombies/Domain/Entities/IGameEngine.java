package com.pvz.plantsvszombies.Domain.Entities;

public interface IGameEngine {
    double getPoint();
    void addPoint(double point);
    void subtractPoint(double point);

    void start();
    void update();

    void disposeObject(IGameObject object);
}
