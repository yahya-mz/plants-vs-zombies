package com.pvz.plantsvszombies.GameEngine;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.GlobalSettings;

import java.util.*;

public class DayEngine implements IGameEngine {

    private final double _windowWidth;
    private final double _windowHeight;

    private final Random _random = new Random();

    private final ArrayList<AbstractGameObject> _gameObjects;
    private MapGameObject _currentMap;

    private final ArrayList<IEventSubscriber> _skySunSpawnEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _mapSpawnEventSubscribers = new ArrayList<>();

    public DayEngine(double windowWidth, double windowHeight) {

        this._windowWidth = windowWidth;
        this._windowHeight = windowHeight;

        this._gameObjects = new ArrayList<>();
    }

    private int tick = 1;
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

    @Override
    public void start() {
        initMap();
    }

    @Override
    public void update() {//updates every object
//        System.out.println("frame: " + tick);
        if (getMilliseconds() % 3000.0 == 0) {
            System.out.println("Droppig");
            dropSunFromSky();
        }
        if (getMilliseconds() % 4000.0 == 0) {
//            PlantSpawnEvent.emit(obj);
        }
        Iterator<AbstractGameObject> iter = _gameObjects.iterator();
        while (iter.hasNext()) {
            AbstractGameObject obj = iter.next();
        }
        for (AbstractGameObject gameObject : _gameObjects) {
            gameObject.update();

        }

        tick++;
    }

    @Override
    public void disposeObject(AbstractGameObject object) {
        _gameObjects.remove(object);
    }

    @Override
    public void spawnObject(AbstractGameObject object) {
//        if (object instanceof AbstractPlantGameObject) {
//
//        } else {
//            for (IEventSubscriber subscriber : _objectSpawnSubscribers) {
//                subscriber._notify(object);
//            }
//        }
        this._gameObjects.add(object);
    }

    @Override
    public void plantObject(AbstractPlantGameObject object) throws Exception {
        if (!_currentMap.isOccupied(object.getRow(), object.getColumn())) {
            this._gameObjects.add(object);//add to array
            subtractPoint(object.getCost());//subtract cost
            _currentMap.plant(object);//callig to plant objectical plant
        } else {
            throw new Exception("Exception: A plant already exists in row:" + object.getRow() + " and col:" + object.getColumn());
        }

    }

    public void subscribeToSkySunSpawnEvent(IEventSubscriber subscriber) {
        this._skySunSpawnEventSubscribers.add(subscriber);
    }

    public void subscribeToMapSpawnEvent(IEventSubscriber subscriber) {
        this._mapSpawnEventSubscribers.add(subscriber);
    }

    private void dropSunFromSky() {
        String sunObjectId = "SKY-SUN_" + UUID.randomUUID();
        Coordinate coordinate = new Coordinate(
                _random.nextDouble(-0.5 * _windowWidth, 0.5 * _windowWidth),
                -_windowHeight / 2
        );
        var sun = SunGameObject.createSunGameObject(this, sunObjectId, coordinate);
        this._gameObjects.add(sun);
        for (IEventSubscriber eventSubscriber : _skySunSpawnEventSubscribers) {
            eventSubscriber._notify(sun);
        }
        sun.spawn();
    }

    private void initMap() {
        String objectId = "MAP_" + UUID.randomUUID();
        Coordinate coordinate = new Coordinate(
                0, 0
        );
        MapGameObject map = MapGameObject.createMapGameObject(5, 9, objectId, coordinate, this);
        this._currentMap = map;

        for (IEventSubscriber subscriber : _mapSpawnEventSubscribers) {
            subscriber._notify(map);
        }
    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

}
