package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.ShroomBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

public class ScaredyShroomGameObject extends AbstractPlantGameObject {

    private final Duration _coolDown = Duration.ofMillis(4000);
    private int tick = 1;

    public boolean isAwake = true;

    private final ArrayList<IEventSubscriber> _shootingEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _switchAwakenessEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();

    public static ScaredyShroomGameObject createScaredy(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new ScaredyShroomGameObject(gameEngine, id, coordinate, row, column);
    }

    private ScaredyShroomGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 0;
    }

    public void subscribeToShootingEvent(IEventSubscriber event) {
        this._shootingEventSubscribers.add(event);
    }

    public void subscribeToEatenEvent(IEventSubscriber event) {
        this._eatenEventSubscribers.add(event);
    }

    public void subscribeToSwitchAwakenessEvent(IEventSubscriber event) {
        this._switchAwakenessEventSubscribers.add(event);
    }

    @Override
    public void spawn() {

    }

    private double _lastShootTick = 0;

    @Override
    public void update() {
        if (!this._isDisposed) {
            tick++;
            var rowsZombies = _gameEngine.getZombiesByRow(_row);
            rowsZombies.sort(Comparator.comparingInt(AbstractZombieGameObject::getColumn));
            if (!rowsZombies.isEmpty()) {
                var frontZombie = rowsZombies.getFirst(); // Most front zombie
                if (frontZombie.getColumn() - _column < 2) {
                    isAwake = false;
                    for (IEventSubscriber eventSubscriber : _switchAwakenessEventSubscribers) {
                        eventSubscriber._notify(this);
                    }
                    _lastShootTick = 0;
                    return;
                }
                if ((_lastShootTick * 1000 / GlobalSettings.FPS) % _coolDown.toMillis() == 0
                        && frontZombie.getColumn() - 4 <= _column) {
                    shoot();
                    _lastShootTick = 0;
                }
                _lastShootTick++;
            } else {
                _lastShootTick = 0;
            }

            isAwake = true;
        }
    }

    @Override
    public void getHit(int damage) {
        _health -= damage;
        if (_health <= 0) {
            eaten();
        }

    }

    private void shoot() {
        String BulletObjectId = "ShroomBullet" + UUID.randomUUID();
        var bulletObj = ShroomBulletGameObject.createShroomBulletGameObject(_gameEngine, BulletObjectId, new Coordinate(this._coordinate.x() + 30, this._coordinate.y() - 20), getRow());
        _gameEngine.spawnObject(bulletObj);
        for (IEventSubscriber eventSubscriber : _shootingEventSubscribers) {
            eventSubscriber._notify(bulletObj);
        }
    }

    private void eaten() {
        for (IEventSubscriber eventSubscriber : _eatenEventSubscribers) {
            eventSubscriber._notify(this);
        }
        super.dispose();
    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

}
