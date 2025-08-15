package com.pvz.plantsvszombies.Domain.Engines;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Common.GameMode;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.*;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.GlobalSettings;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DayEngine extends GameEngine {
    private final Duration _skySunDroppingInterval = Duration.ofSeconds(7);
    private Duration _zombieSpawnInterval = Duration.ofSeconds(3);
    private final Duration _wave_2_Start = Duration.ofSeconds(15);
    private final Duration _wave_3_Start = Duration.ofSeconds(30);
    private final Duration _wave_4_Start = Duration.ofSeconds(45);
    private final Duration _gameInterval = Duration.ofSeconds(60);

    private final Random _zombieSpawnRandom = new Random(System.currentTimeMillis());
    private final Random _skyDroppingRandom = new Random(System.currentTimeMillis() * 100);
    private final Random _zombieTypeRandom = new Random(System.currentTimeMillis() / 1000);

    private final ArrayList<IEventSubscriber> _midAttackEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _finalAttackEventSubscribers = new ArrayList<>();

    public DayEngine(double windowWidth, double windowHeight) {
        this._windowWidth = windowWidth;
        this._windowHeight = windowHeight;
        this._gameMode = GameMode.DAY;

        this._gameObjects = new CopyOnWriteArrayList<>();
    }

    private int _currentWave = 1;

    @Override
    public void start() {
        initMap();
    }

    //updates every object
    @Override
    public void update() {

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

        // Zombie Spawning
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
    public void load() {

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

    private void initMap() {
        String objectId = "MAP_" + UUID.randomUUID();
        Coordinate coordinate = new Coordinate(
                0, 0
        );
        MapGameObject map = MapGameObject.createMapGameObject(objectId, coordinate, this);
        _currentMap = map;

        for (IEventSubscriber subscriber : _gameObjectSpawnEventSubscribers) {
            subscriber._notify(map);
        }
    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

}
