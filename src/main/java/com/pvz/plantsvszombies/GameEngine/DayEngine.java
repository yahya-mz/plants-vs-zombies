package com.pvz.plantsvszombies.GameEngine;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.NormalZombieGameObject;
import com.pvz.plantsvszombies.GlobalSettings;

import java.util.*;

public class DayEngine implements IGameEngine {

    private final double _windowWidth;
    private final double _windowHeight;

    private final Random _random = new Random();

    private final ArrayList<AbstractGameObject> _gameObjects;
    private MapGameObject _currentMap;

    private final ArrayList<IEventSubscriber> _gameObjectSpawnEventSubscribers = new ArrayList<>();
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

    private boolean demo = true;

    @Override
    public void update() {  //updates every object
        //System.out.println("frame: " + tick);
        if (getMilliseconds() % 3000.0 == 0) {
            System.out.println("Droppig");
            dropSunFromSky();
            if (demo) {
                spawnZombie();
                demo = false;
            }
        }
        Iterator<AbstractGameObject> iter = _gameObjects.iterator();
        while (iter.hasNext()) {
            AbstractGameObject gameObject = iter.next();
            if (gameObject.getCoordinate().x() > this._windowWidth
                    || gameObject.getCoordinate().y() > this._windowHeight
                    || gameObject.getCoordinate().x() < 0
                    || gameObject.getCoordinate().y() < 0) {
                _gameObjects.remove(gameObject);
                continue;
            }
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

    @Override
    public AbstractPlantGameObject getPlantAtBlock(int row, int column) {
        synchronized (_currentMap) {
            return this._currentMap.getPlantAtBlock(row, column);
        }
    }

    @Override
    public List<AbstractGameObject> getGameObjects() {
        return this._gameObjects.stream().toList();
    }

    public void subscribeToGameObjectSpawnEvent(IEventSubscriber subscriber) {
        this._gameObjectSpawnEventSubscribers.add(subscriber);
    }

    public void subscribeToMapSpawnEvent(IEventSubscriber subscriber) {
        this._mapSpawnEventSubscribers.add(subscriber);
    }

    private void dropSunFromSky() {
        String sunObjectId = "SKY-SUN_" + UUID.randomUUID();
        Coordinate coordinate = new Coordinate(
                _random.nextDouble(0.2 * _windowWidth, 0.8 * _windowWidth),
                0
        );
        var sun = SunGameObject.createSunGameObject(this, sunObjectId, coordinate);
        this._gameObjects.add(sun);
        for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) {
            eventSubscriber._notify(sun);
        }
    }

    private void spawnZombie() {
        String zombieObjectId = "Zombie" + UUID.randomUUID();
        var lastBlock = _currentMap.getBlock(0, 8);
        Coordinate coordinate = lastBlock.getTopLeftCoordinate();
        var zombie = NormalZombieGameObject.createNormalZombieGameObject(this, zombieObjectId, coordinate, 0, 8);
        this._gameObjects.add(zombie);
        for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) {
            eventSubscriber._notify(zombie);
        }
    }

    private void initMap() {
        String objectId = "MAP_" + UUID.randomUUID();
        Coordinate coordinate = new Coordinate(
                0, 0
        );
        MapGameObject map = MapGameObject.createMapGameObject(5, 9, objectId, coordinate, this);
        _currentMap = map;

        for (IEventSubscriber subscriber : _mapSpawnEventSubscribers) {
            subscriber._notify(map);
        }
    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

}
