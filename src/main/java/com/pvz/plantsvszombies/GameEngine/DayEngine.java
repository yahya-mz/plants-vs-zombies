package com.pvz.plantsvszombies.GameEngine;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Events.PlantSpawnEvent;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;
import com.pvz.plantsvszombies.Domain.Entities.IGameObject;
import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeaShooterGameObject;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.GlobalSettings;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class DayEngine implements IGameEngine {

    private final double _windowWidth;
    private final double _windowHeight;

    private final Random _random = new Random();

    private final ArrayList<IGameObject> _gameObjects;

    public DayEngine(double windowWidth, double windowHeight) {

        this._windowWidth = windowWidth;
        this._windowHeight = windowHeight;

        this._gameObjects = new ArrayList<>();
    }

    private int tick = 1;
    private int milliseconds;

    private double _point;

    @Override
    public double getPoint() {
        return _point;
    }

    @Override
    public void addPoint(double point) {
        this._point += point;
    }

    @Override
    public void subtractPoint(double point) {
        this._point -= point;
    }

    public void start() {
        initMap();
    }

    public void update() {
//        System.out.println("frame: " + tick);
        if (getMilliseconds() % 3000.0 == 0) {
            System.out.println("Droppig");
            dropSunFromSky();
        }
        if (getMilliseconds() % 4000.0 == 0) {
            String sunObjectId = "SKY-SUN_" + UUID.randomUUID();
            Coordinate coordinate = new Coordinate(
                    _random.nextDouble(-0.5 * _windowWidth, 0.5 * _windowWidth),
                    -_windowHeight / 2
            );
//            var obj = PeaShooterGameObject.createSunGameObject();
//            PlantSpawnEvent.emit(MapGameObject.createMapGameObject());
        }

        tick++;
    }

    @Override
    public void disposeObject(IGameObject object) {
        _gameObjects.remove(object);
    }

    private void dropSunFromSky() {
        String sunObjectId = "SKY-SUN_" + UUID.randomUUID();
        Coordinate coordinate = new Coordinate(
                _random.nextDouble(-0.5 * _windowWidth, 0.5 * _windowWidth),
                -_windowHeight / 2
        );
        var sun = SunGameObject.createSunGameObject(this, sunObjectId, coordinate);
        this._gameObjects.add(sun);
        sun.spawn();
    }

    private void initMap() {
        String objectId = "MAP_" + UUID.randomUUID();
        Coordinate coordinate = new Coordinate(
                0, 0
        );
        MapGameObject map = MapGameObject.createMapGameObject(5, 9, objectId, coordinate);
        map.spawn();
    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

}
