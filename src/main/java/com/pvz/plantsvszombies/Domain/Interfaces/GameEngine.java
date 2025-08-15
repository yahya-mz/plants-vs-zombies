package com.pvz.plantsvszombies.Domain.Interfaces;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.stream.Collectors;

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
    protected final ArrayList<GraveGameObject> _graves = new ArrayList<>();

    public abstract void start();

    public abstract void update();


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
        if (object instanceof AbstractPlantGameObject p) {
            _currentMap.getBlock(p.getRow(), p.getColumn()).setPlant(null);
        } else if (object instanceof GraveGameObject g) {
            _currentMap.getBlock(g.getRow(), g.getColumn()).setGrave(null);
        }
    }

    public void spawnObject(AbstractGameObject object) {
        this._gameObjects.add(object);

        for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) { // notify visual engine to spawn the plant
            eventSubscriber._notify(object);
        }
    }

    public void plantObject(AbstractPlantGameObject object) throws Exception {
        boolean isGraveBuster = object instanceof com.pvz.plantsvszombies.Domain.Entities.Plants.GraveBusterGameObject;


        if (isGraveBuster) {
            if (_currentMap.getBlock(object.getRow(), object.getColumn()).getGrave() == null) {
                throw new Exception("Exception: No grave exists in row:" + object.getRow() + " col:" + object.getColumn() + " for GraveBuster.");
            }
            // GraveBuster را فقط اسپاون کن (روی بلاک setPlant نکن)
            this._gameObjects.add(object);
            subtractPoint(object.getCost());

            for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) {
                eventSubscriber._notify(object);
            }
            return;
        }

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

    public void spawnGrave(int row, int col) {
        String graveObjectId = "Grave" + UUID.randomUUID();
        var block = _currentMap.getBlock(row, col);
        Coordinate coordinate = block.getCenterCoordinate();

        GraveGameObject grave = GraveGameObject.createGraveGameObject(this, graveObjectId,
                coordinate.copy(), row, col);

        // ثبت روی بلاک (هر کدوم که ساختی)
         block.setGrave(grave);
        _currentMap.placeGrave(grave);
         _graves.add(grave);

        this._gameObjects.add(grave);
        for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) {
            eventSubscriber._notify(grave);
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

    public List<FogGameObject> queryFogs(Predicate<FogGameObject> predicate) {
        return _gameObjects.stream()
                .filter(obj -> obj instanceof FogGameObject &&
                        predicate.test((FogGameObject) obj))
                .map(obj -> (FogGameObject) obj)
                .toList();
    }

    public FogGameObject queryFog(Predicate<FogGameObject> predicate) {
        return _gameObjects.stream()
                .filter(obj -> obj instanceof FogGameObject &&
                        predicate.test((FogGameObject) obj))
                .map(obj -> (FogGameObject) obj)
                .findFirst()
                .orElse(null);
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
