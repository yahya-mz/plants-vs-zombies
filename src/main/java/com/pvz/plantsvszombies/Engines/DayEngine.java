package com.pvz.plantsvszombies.Engines;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.NormalZombieGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Interfaces.IGameEngine;
import com.pvz.plantsvszombies.GlobalSettings;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class DayEngine implements IGameEngine {

    private final double _windowWidth;
    private final double _windowHeight;

    private final int _rows = 5;
    private final int _columns = 9;


    private final Duration _skySunDroppingInterval = Duration.ofSeconds(7);
    private Duration _zombieSpawnInterval = Duration.ofSeconds(3);
    private final Duration _wave_2_Start = Duration.ofSeconds(15);
    private final Duration _wave_3_Start = Duration.ofSeconds(30);
    private final Duration _wave_4_Start = Duration.ofSeconds(45);
    private final Duration _gameInterval = Duration.ofSeconds(60);

    private final Random _zombieSpawnRandom = new Random(System.currentTimeMillis());
    private final Random _skyDroppingRandom = new Random(System.currentTimeMillis() * 100);
    private final Random _zombieTypeRandom = new Random(System.currentTimeMillis() / 1000);


    private final CopyOnWriteArrayList<AbstractGameObject> _gameObjects;
    private MapGameObject _currentMap;

    private final ArrayList<IEventSubscriber> _gameObjectSpawnEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _mapSpawnEventSubscribers = new ArrayList<>();

    public DayEngine(double windowWidth, double windowHeight) {

        this._windowWidth = windowWidth;
        this._windowHeight = windowHeight;

        this._gameObjects = new CopyOnWriteArrayList<>();
    }

    private int tick = 1;
    private double _point;
    private int _currentWave = 1;

    @Override
    public int getRowsCount() {
        return this._rows;
    }

    @Override
    public int getColumnsCount() {
        return this._columns;
    }

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

        // Checking wave changes:
        if (getMilliseconds() % _gameInterval.toMillis() == 0) {
            System.out.println("GameOver !");
        } else if (getMilliseconds() == _wave_4_Start.toMillis()) {
            _currentWave = 4;
            System.out.println("Wave 4");
        } else if (getMilliseconds() == _wave_3_Start.toMillis()) {
            _currentWave = 3;
            System.out.println("Wave 3");
        } else if (getMilliseconds() == _wave_2_Start.toMillis()) {
            _currentWave = 2;
            System.out.println("Wave 2");
        }

        // Sun dropping:
        if (getMilliseconds() % _skySunDroppingInterval.toMillis() == 0) {
            dropSunFromSky();
        }

        // Zombie
        switch (_currentWave) {
            case 1 -> {
                if (getMilliseconds() % 3000.0 == 0) {
                    var row = _zombieSpawnRandom.nextInt(0, _rows);
                    spawnZombie(row, _columns - 1, AbstractZombieGameObject.ZombieType.NORMAL_ZOMBIE);
                }
            }
            case 2 -> {
                if (getMilliseconds() % 2000.0 == 0) {
                    var row = _zombieSpawnRandom.nextInt(0, _rows);
                    var zombieTypes = new AbstractZombieGameObject.ZombieType[]{AbstractZombieGameObject.ZombieType.NORMAL_ZOMBIE,
                            AbstractZombieGameObject.ZombieType.CONE_HEAD_ZOMBIE};
                    spawnZombie(row, _columns - 1, zombieTypes[_zombieTypeRandom.nextInt(zombieTypes.length)]);
                }
            }
            case 3 -> {
                if (getMilliseconds() % 2000.0 == 0) {
                    var row = _zombieSpawnRandom.nextInt(0, _rows);
                    var zombieTypes = new AbstractZombieGameObject.ZombieType[]{AbstractZombieGameObject.ZombieType.NORMAL_ZOMBIE,
                            AbstractZombieGameObject.ZombieType.CONE_HEAD_ZOMBIE, AbstractZombieGameObject.ZombieType.SCREEN_DOOR_ZOMBIE};
                    spawnZombie(row, _columns - 1, zombieTypes[_zombieTypeRandom.nextInt(zombieTypes.length)]);
                }
            }
            case 4 -> {
                if (getMilliseconds() % 2000.0 == 0) {
                    var row = _zombieSpawnRandom.nextInt(0, _rows);
                    spawnZombie(row, _columns - 1, AbstractZombieGameObject.ZombieType.values()[_zombieTypeRandom.nextInt(AbstractZombieGameObject.ZombieType.values().length)]);
                }
            }
        }

//        List<AbstractGameObject> toRemove = new ArrayList<>();

        synchronized (_gameObjects) {
            for (AbstractGameObject gameObject : _gameObjects) {
                gameObject.update(); // Safe now
                if (gameObject.getCoordinate().x() > this._windowWidth
                        || gameObject.getCoordinate().y() > this._windowHeight
                        || gameObject.getCoordinate().x() < 0
                        || gameObject.getCoordinate().y() < 0) {
//                    toRemove.add(gameObject); // Just mark for removal
                    gameObject.dispose();
                }
            }
//            _gameObjects.removeAll(toRemove); // Remove after iteration — safe
        }


        tick++;
    }

    @Override
    public void disposeObject(AbstractGameObject object) {
        _gameObjects.remove(object);
        if (object instanceof AbstractPlantGameObject) {
            _currentMap.getBlock(((AbstractPlantGameObject) object).getRow(),
                    ((AbstractPlantGameObject) object).getColumn()).setPlant(null);
        }
    }

    @Override
    public void spawnObject(AbstractGameObject object) {
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
        return this._currentMap.getPlantAtBlock(row, column);
    }

    @Override
    public MapBlock getBlockByCoordinate(Coordinate coordinate) {
        return this._currentMap.getBlockByCoordinate(coordinate);
    }

    @Override
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
                _skyDroppingRandom.nextDouble(0.2 * _windowWidth, 0.8 * _windowWidth),
                0
        );
        var sun = SunGameObject.createSunGameObject(this, sunObjectId, coordinate);
        this._gameObjects.add(sun);
        for (IEventSubscriber eventSubscriber : _gameObjectSpawnEventSubscribers) {
            eventSubscriber._notify(sun);
        }
    }

    private void spawnZombie(int row, int col, AbstractZombieGameObject.ZombieType zombieType) {
        String zombieObjectId = "Zombie" + UUID.randomUUID();
        var block = _currentMap.getBlock(row, col);
        Coordinate coordinate = block.getCenterCoordinate();
        AbstractZombieGameObject zombie = switch (zombieType) {
            case NORMAL_ZOMBIE -> NormalZombieGameObject.createNormalZombieGameObject(this, zombieObjectId,
                    coordinate.copy(), row, col); // Here if you pass 'coordinate' as coordinate.copy(), all zombies will share a same coordinate
            default -> NormalZombieGameObject.createNormalZombieGameObject(this, zombieObjectId,
                    coordinate.copy(), row, col);
        };

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
        MapGameObject map = MapGameObject.createMapGameObject(objectId, coordinate, this);
        _currentMap = map;

        for (IEventSubscriber subscriber : _mapSpawnEventSubscribers) {
            subscriber._notify(map);
        }
    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

}
