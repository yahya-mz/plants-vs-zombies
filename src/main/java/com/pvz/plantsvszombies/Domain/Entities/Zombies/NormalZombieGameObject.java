package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.AbstractBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.MapBlock;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Interfaces.IGameEngine;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.GlobalSettings;

import java.time.Duration;
import java.util.ArrayList;

public class NormalZombieGameObject extends AbstractZombieGameObject {

    public final static Duration BITE_COOL_DOWN = Duration.ofMillis(500);

    private int tick = 1;

    private enum State {
        MOVING,
        EATING,
        DEAD
    }

    private State _state;

    private final ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _eatingEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _burnEventSubscribers = new ArrayList<>();
    private final ArrayList<IEventSubscriber> _deathEventSubscribers = new ArrayList<>();

    public static NormalZombieGameObject createNormalZombieGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new NormalZombieGameObject(gameEngine, id, coordinate, row, column);
    }

    NormalZombieGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._health = 125;

        this._state = State.MOVING;
    }

    public void subscribeToMovementEvent(IEventSubscriber eventSubscriber) {
        this._movementEventSubscribers.add(eventSubscriber);
    }

    public void subscribeToEatingEvent(IEventSubscriber eventSubscriber) {
        this._eatingEventSubscribers.add(eventSubscriber);
    }

    public void subscribeToDeathEvent(IEventSubscriber eventSubscriber) {
        this._deathEventSubscribers.add(eventSubscriber);
    }

    public void subscribeToBurnEvent(IEventSubscriber eventSubscriber) {
        this._burnEventSubscribers.add(eventSubscriber);

    }

    @Override
    public void spawn() {

    }

    private double lastBiteMillis = -1;

    @Override
    public void update() {
        if (!_isDisposed) {
            checkCollision();
            if (_state.equals(State.DEAD)) {
                return;
            }
            var zombieBlock = _gameEngine.getBlockByCoordinate(this._coordinate);
            if (zombieBlock != null) {
                this._column = zombieBlock.getColumn(); // Update Current Block
                if (zombieBlock.getPlant() != null) {
                    eat(zombieBlock.getPlant());
                    tick++;
                    return;
                }
            }
            lastBiteMillis = -1;
            this._state = State.MOVING;
            move();
        }
        tick++;
    }

    @Override
    public void getHit(AbstractBulletGameObject bullet) {
        if (bullet.getBulletType().equals(AbstractBulletGameObject.BulletType.NORMAL_BULLET)) {
            _health -= bullet.getDamage();
        }
        if (_health <= 0) {
            die();
        }
    }

    @Override
    public void getBurned() {
        for (IEventSubscriber eventSubscriber : _burnEventSubscribers) {
            eventSubscriber._notify(this);
        }
        super.dispose();
    }

    @Override
    public double getSpeed() {
        return _speed;
    }

    private void move() {
        this._coordinate.traverse(-1 * _speed, 0);
        for (IEventSubscriber subscriber : _movementEventSubscribers) {
            subscriber._notify(this);
        }
    }

    private void checkCollision() {
        for (AbstractGameObject obj : _gameEngine.getGameObjects()) {
            if (obj instanceof AbstractBulletGameObject) {
                if (((AbstractBulletGameObject) obj).getRow() == this._row
                        && Math.abs(((AbstractBulletGameObject) obj).getCoordinate().x() - this._coordinate.x()) < (this._speed + ((AbstractBulletGameObject) obj).getSpeed())) {
                    getHit((AbstractBulletGameObject) obj);
                    ((AbstractBulletGameObject) obj).collide(this);
                }
            }
        }
    }

    private void eat(AbstractPlantGameObject plant) {
        if (lastBiteMillis == -1) { // Start eating
            lastBiteMillis = getMilliseconds();
            this._state = State.EATING;
            for (IEventSubscriber subscriber : _eatingEventSubscribers) {
                subscriber._notify(this);
            }
        }
        if (getMilliseconds() - lastBiteMillis > BITE_COOL_DOWN.toMillis()) {
            plant.getHit(this._damage);
            lastBiteMillis = getMilliseconds();
        }
    }

    private void die() {
//        this._gameEngine = null; Believe it or not but this makes the _engine property null for all the existing ZombieGameObjects in the App
        _state = State.DEAD;
        for (IEventSubscriber eventSubscriber : _deathEventSubscribers) {
            eventSubscriber._notify(this);
        }
        super.dispose();

    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }
}
