package com.pvz.plantsvszombies.Domain.Engines;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Common.GameMode;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.*;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Services.PersistenceManager;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Mediator.Mediator;
import javafx.application.Platform;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NightEngine extends GameEngine {
    private final Duration _zombieSpawnInterval = Duration.ofSeconds(3);
    private final Duration _wave_2_Start = Duration.ofSeconds(15);
    private final Duration _wave_3_Start = Duration.ofSeconds(30);
    private final Duration _wave_4_Start = Duration.ofSeconds(45);
    private final Duration _gameInterval = Duration.ofSeconds(5);

    private final Random _zombieSpawnRandom = new Random(System.currentTimeMillis());
    private final Random _zombieTypeRandom = new Random(System.currentTimeMillis() / 1000);

    private final ArrayList<FogGameObject> _fogs = new ArrayList<>();



    private final ArrayList<IEventSubscriber> _midAttackEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _finalAttackEventSubscribers = new ArrayList<>();

    public NightEngine(double windowWidth, double windowHeight) {
        this._windowWidth = windowWidth;
        this._windowHeight = windowHeight;
        this._gameMode = GameMode.NIGHT;
        this._gameObjects = new CopyOnWriteArrayList<>();
    }

    private int _currentWave = 1;

    @Override
    public void start() {
//        load();
        initMap();
        _currentMap.subscribeToBlocksReadyEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                initFog();
                initGraves();
            }
        });
    }

    //updates every object
    @Override
    public void update() {
        if (getMilliseconds() % 30000 == 0) {
//            exportObjects();
        }

        // Checking wave changes:
        if (getMilliseconds() % _gameInterval.toMillis() == 0) {
            win();
        } else if (getMilliseconds() == _wave_4_Start.toMillis()) {
            _currentWave = 4;
            System.out.println("Wave 4");

        } else if (getMilliseconds() == _wave_3_Start.toMillis()) {
            _currentWave = 3;
            System.out.println("Wave 3");
        } else if (getMilliseconds() == _wave_2_Start.toMillis()) {
            _currentWave = 2;
            convertAllGravesToZombies(); // ← همین‌جا
            System.out.println("Wave 2");
        }

        // Zombie Spawning
        switch (_currentWave) {
            case 1 -> {
                if (getMilliseconds() % 10000.0 == 0) {
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

                // Stop zombie spawning
                _currentWave = -1;
                System.out.println("Zombie spawning stopped");
            }
            case -1 -> {
                if (_gameObjects.stream().noneMatch(z -> z instanceof AbstractZombieGameObject)) {
                    System.out.println("You Won");
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
                    if (gameObject instanceof FogGameObject) {
                        System.out.println("fog");
                    }
                    gameObject.dispose(true);
                }
            }
//            _gameObjects.removeAll(toRemove); // Remove after iteration — safe
        }

        tick++;
    }

    public ArrayList<FogGameObject> getFogs() {
        return this._fogs;
    }

    private void initMap() {
        String objectId = "MAP_" + UUID.randomUUID();
        Coordinate coordinate = new Coordinate(
                0, 0
        );
        MapGameObject map = MapGameObject.createMapGameObject(objectId, coordinate, this);
        _currentMap = map;

        _gameObjects.add(map);
        for (IEventSubscriber subscriber : _gameObjectSpawnEventSubscribers) {
            subscriber._notify(map);
        }
    }

    private void initFog() {
        for (int i = 0; i < 5; i++) {
            for (int j = 6; j <= 8; j++) {
                var fogBlock = getBlock(i, j);
                var coordinate = fogBlock.getTopLeftCoordinate();
                var fog = FogGameObject.createFogGameObject(this, "Fog_" + UUID.randomUUID(),
                        coordinate, i, j);
                spawnObject(fog);
                _fogs.add(fog);
            }
        }
    }

    private void initGraves() {
        java.util.List<int[]> candidates = new java.util.ArrayList<>();
        for (int r = 0; r < _rows; r++) {
            for (int c = _columns - 3; c < _columns; c++) {
                candidates.add(new int[]{r, c});
            }
        }
        java.util.Collections.shuffle(candidates, new java.util.Random(
                System.currentTimeMillis() ^ 0x9E3779B97F4A7C15L));

        int gravesToSpawn = Math.min(5, candidates.size());
        int placed = 0;

        for (int k = 0; k < candidates.size() && placed < gravesToSpawn; k++) {
            int row = candidates.get(k)[0];
            int col = candidates.get(k)[1];

            // این چک الان isOccupied است و قبر را هم شامل می‌شود
            if (_currentMap.isOccupied(row, col)) continue;

            spawnGrave(row, col);
            placed++;
        }
    }
    private boolean _gravesConverted = false;

    private void convertAllGravesToZombies() {
        if (_gravesConverted) return;

        for (GraveGameObject grave : new ArrayList<>(_graves)) {
            int row = grave.getRow();
            int col = grave.getColumn();

            grave.dispose(true);
            _graves.remove(grave);

            spawnZombie(row, col, AbstractZombieGameObject.ZombieType.NORMAL_ZOMBIE);
        }

        _gravesConverted = true;
    }

    @Override
    public void load() {
        _gameObjects.clear();
        var objects = PersistenceManager.load();
        objects.forEach(e -> {
            Platform.runLater(() -> {
                e.setGameEngine(this);
                _gameObjects.add(e);
                switch (e) {
                    case MapGameObject mapGameObject -> {
                        Mediator.getInstance().getVisualEngine()
                                .spawnGameObject(e);

//                        for (IEventSubscriber subscriber : _gameObjectSpawnEventSubscribers) {
//                            subscriber._notify(e);
//                        }

                        _currentMap = mapGameObject;

                    }
//                    case AbstractPlantGameObject plant -> {
//                        try {
//                            plantObject(plant);
//                        } catch (Exception _) {
//
//                        }
//                    }
                    default -> Mediator.getInstance().getVisualEngine()
                            .spawnGameObject(e);
                }
            });
        });

    }
    public int get_currentWave() {return _currentWave;}
    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

}
