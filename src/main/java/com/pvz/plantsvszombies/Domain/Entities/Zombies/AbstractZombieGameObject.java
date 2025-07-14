package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.AbstractBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.MapBlock;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

import java.time.Duration;
import java.util.ArrayList;

public abstract class AbstractZombieGameObject extends AbstractGameObject {

    public enum ZombieType {
        NORMAL_ZOMBIE,
        CONE_HEAD_ZOMBIE,
        SCREEN_DOOR_ZOMBIE,
        IMP_ZOMBIE
    }

    protected interface ZombieState {
    }

    public enum GeneralZombieState implements ZombieState {
        EATING,
        MOVING,
        DEAD
    }

    protected int _tick;

    protected int _row;
    protected int _column;

    protected double _health;
    protected int _damage = 25;
    protected ZombieState _state = GeneralZombieState.MOVING;
    protected Duration _biteCoolDown = Duration.ofMillis(500);
    protected double _speed = (double) 1 / 4 * MapBlock.BLOCK_SIZE / GlobalSettings.FPS;
    // ( 1 block / 1 sec) * ( 1 pixel / 1 block ) * ( 1 sec / 1 frame ) = ( pixel / frame )


    protected final ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();

    protected final ArrayList<IEventSubscriber> _eatingEventSubscribers = new ArrayList<>();

    protected final ArrayList<IEventSubscriber> _burnEventSubscribers = new ArrayList<>();

    protected final ArrayList<IEventSubscriber> _deathEventSubscribers = new ArrayList<>();

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
    public void update() {
        if (!_isDisposed) {
            checkCollision();
            if (_state.equals(GeneralZombieState.DEAD)) {
                return;
            }
            var zombieBlock = _gameEngine.getBlockByCoordinate(this._coordinate);
            if (zombieBlock != null) {
                this._column = zombieBlock.getColumn(); // Update Current Block
                if (zombieBlock.getPlant() != null) {
                    eat(zombieBlock.getPlant());
                    _tick++;
                    return;
                }
            } else {
                if (this._gameEngine.getBlock(0, 0).getTopLeftCoordinate().x() > this._coordinate.x()) {
                    _gameEngine.lose(this);
                }
            }
            lastBiteMillis = -1;
            this._state = GeneralZombieState.MOVING;
            move();
        }
        _tick++;
    }

    public int getRow() {
        return this._row;
    }

    public int getColumn() {
        return this._column;
    }

    public double getHealth() {
        return this._health;
    }

    public double getSpeed() {
        return this._speed;
    }

    public void getHit(AbstractBulletGameObject bullet) {
        if (bullet.getBulletType().equals(AbstractBulletGameObject.BulletType.NORMAL_BULLET)) {
            _health -= bullet.getDamage();
        }
        if (_health <= 0) {
            die();
        }
    }

    public void getBurned() {
        for (IEventSubscriber eventSubscriber : _burnEventSubscribers) {
            eventSubscriber._notify(this);
        }
        super.dispose();
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

    private void die() {
//        this._gameEngine = null; Believe it or not but this makes the _engine property null for all the existing ZombieGameObjects in the App
        _state = GeneralZombieState.DEAD;
        for (IEventSubscriber eventSubscriber : _deathEventSubscribers) {
            eventSubscriber._notify(this);
        }
        super.dispose();

    }

    private double lastBiteMillis = -1;

    private void eat(AbstractPlantGameObject plant) {
        if (lastBiteMillis == -1) { // Start eating
            lastBiteMillis = getMilliseconds();
            this._state = GeneralZombieState.EATING;
            for (IEventSubscriber subscriber : _eatingEventSubscribers) {
                subscriber._notify(this);
            }
        }
        if (getMilliseconds() - lastBiteMillis > _biteCoolDown.toMillis()) {
            plant.getHit(this._damage);
            lastBiteMillis = getMilliseconds();
        }
    }

    private double getMilliseconds() {
        return this._tick * 1000.0 / GlobalSettings.FPS;
    }

}
