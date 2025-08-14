package com.pvz.plantsvszombies.Domain.Interfaces;


import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.*;
import com.pvz.plantsvszombies.Domain.Services.PersistenceManager;
import com.pvz.plantsvszombies.Mediator.Mediator;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public abstract class GameEngine {
    protected double _windowWidth;
    protected double _windowHeight;

    protected final int _rows = 5;
    protected final int _columns = 9;

    protected int tick = 1;
    protected double _point;

    protected CopyOnWriteArrayList<AbstractGameObject> _gameObjects;
    protected MapGameObject _currentMap;

    protected final ArrayList<IEventSubscriber> _gameObjectSpawnEventSubscribers = new ArrayList<>();

    protected final ArrayList<IEventSubscriber> _winEventSubscribers = new ArrayList<>();
    protected final ArrayList<IEventSubscriber> _lostEventSubscribers = new ArrayList<>();

    public abstract void start();

    public abstract void update();

    public abstract void load();

    public void subscribeToGameObjectSpawnEvent(IEventSubscriber eventSubscriber) {
        this._gameObjectSpawnEventSubscribers.add(eventSubscriber);
    }

    public void subscribeToLostEvent(IEventSubscriber eventSubscriber) {
        this._lostEventSubscribers.add(eventSubscriber);
    }

    public void subscribeToWinEvent(IEventSubscriber eventSubscriber) {
        this._winEventSubscribers.add(eventSubscriber);
    }


    public int getRowsCount() {
        return this._rows;
    }

    public int getColumnsCount() {
        return this._columns;
    }

    public double getPoint() {
        return _point;
    }

    public double getWindowWidth() {
        return _windowWidth;
    }

    public double getWindowHeight() {
        return _windowHeight;
    }

    public void addPoint(double point) {
        this._point += point;
    }

    public void subtractPoint(double point) {
        this._point -= point;
    }

    public void disposeObject(AbstractGameObject object) {
        _gameObjects.remove(object);
        if (object instanceof AbstractPlantGameObject) {
            _currentMap.getBlock(((AbstractPlantGameObject) object).getRow(),
                    ((AbstractPlantGameObject) object).getColumn()).setPlant(null);
        }
    }

    public void spawnObject(AbstractGameObject object) {
        this._gameObjects.add(object);

        for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) { // notify visual engine to spawn the plant
            eventSubscriber._notify(object);
        }
    }

    public void plantObject(AbstractPlantGameObject object) throws Exception {
        if (!_currentMap.isOccupied(object.getRow(), object.getColumn())) {
            this._gameObjects.add(object); // add to game objects list

            subtractPoint(object.getCost()); // subtract cost

            _currentMap.plant(object); // apply game object (logic) first, add plant game object to map game objects

            for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) { // notify visual engine to spawn the plant
                eventSubscriber._notify(object);
            }

        } else {
            throw new Exception("Exception: A plant already exists in row:" + object.getRow() + " and col:" + object.getColumn());
        }

    }

    public AbstractPlantGameObject getPlantAtBlock(int row, int column) {
        return this._currentMap.getPlantAtBlock(row, column);
    }

    public MapBlock getBlock(int row, int column) {
        return this._currentMap.getBlock(row, column);
    }

    public MapBlock getBlockByCoordinate(Coordinate coordinate) {
        return this._currentMap.getBlockByCoordinate(coordinate);
    }

    public void spawnZombie(int row, int col, AbstractZombieGameObject.ZombieType zombieType) {
        String zombieObjectId = "Zombie" + UUID.randomUUID();
        var block = _currentMap.getBlock(row, col);
        Coordinate coordinate = block.getCenterCoordinate();
        AbstractZombieGameObject zombie = switch (zombieType) {
            case NORMAL_ZOMBIE -> NormalZombieGameObject.createNormalZombieGameObject(this, zombieObjectId,
                    coordinate.copy(), row, col);
            case CONE_HEAD_ZOMBIE -> ConeHeadZombieGameObject.createConeHeadZombieGameObject(this, zombieObjectId,
                    coordinate.copy(), row, col);
            case IMP_ZOMBIE -> ImpZombieGameObject.createImpZombieGameObject(this, zombieObjectId,
                    coordinate.copy(), row, col);
            case SCREEN_DOOR_ZOMBIE -> ScreenDoorZombieGameObject.createScreenDoorZombieGameObject(this, zombieObjectId,
                    coordinate.copy(), row, col); // Here if you pass 'coordinate' as coordinate.copy(), all zombies will share a same coordinate
        };

        this._gameObjects.add(zombie);
        for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) {
            eventSubscriber._notify(zombie);
        }
    }

    public List<AbstractZombieGameObject> queryZombies(Predicate<AbstractZombieGameObject> predicate) {
        return _gameObjects.stream().filter(obj -> obj instanceof AbstractZombieGameObject &&
                        (predicate.test((AbstractZombieGameObject) obj))).map(obj -> (AbstractZombieGameObject) obj)
                .toList();
    }

    public AbstractZombieGameObject queryZombie(Predicate<AbstractZombieGameObject> predicate) {
        return _gameObjects.stream().filter(obj -> obj instanceof AbstractZombieGameObject &&
                        (predicate.test((AbstractZombieGameObject) obj))).map(obj -> (AbstractZombieGameObject) obj)
                .findFirst().orElse(null);
    }

    public List<AbstractGameObject> getGameObjects() {
        return this._gameObjects.stream().toList();
    }

    public boolean doesRowHaveZombie(int row) {
        for (AbstractGameObject obj : _gameObjects) {
            if (obj instanceof AbstractZombieGameObject) {
                if (((AbstractZombieGameObject) obj).getRow() == row) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<AbstractZombieGameObject> getZombiesByRow(int row) {
        return queryZombies(z -> z.getRow() == row)
                .stream().sorted(Comparator.comparingInt(AbstractZombieGameObject::getColumn)).toList();
    }

    public HypnotizedZombieGameObject queryHypnotizedZombie(Predicate<HypnotizedZombieGameObject> predicate) {
        return _gameObjects.stream().filter(obj -> obj instanceof HypnotizedZombieGameObject &&
                        (predicate.test((HypnotizedZombieGameObject) obj))).map(obj -> (HypnotizedZombieGameObject) obj)
                .findFirst().orElse(null);
    }

    public void lose(AbstractZombieGameObject winnerZombie) {
        Mediator.getInstance().stopGameEngine();
        for (IEventSubscriber eventSubscriber : _lostEventSubscribers) {
            eventSubscriber._notify(winnerZombie);
        }
    }

    public void win() {
        Mediator.getInstance().stopGameEngine();
        for (IEventSubscriber eventSubscriber : _winEventSubscribers) {
            eventSubscriber._notify(null);
        }
    }

    public void exportObjects() {
        PersistenceManager.saveAsDat(_gameObjects.stream().toList());

    }

    public void importObjects(ArrayList<AbstractGameObject> objects) {
        this._gameObjects.clear();
        this._gameObjects.addAll(objects);

//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValue(new File("target/car.json"), car);
    }
}
