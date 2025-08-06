package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.AbstractBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.HypnotizedZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.MapBlock;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.HypnoShroomGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

public abstract class AbstractZombieGameObject extends AbstractGameObject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;  // SAME VALUE

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
        DEAD,
        HYPNOTIZED
    }

    protected int _tick;

    protected int _row;
    protected int _column;

    protected double _health;
    protected int _damage = 25;
    protected ZombieState _state = GeneralZombieState.MOVING;
    protected Duration _biteCoolDown = Duration.ofMillis(500);
    protected boolean _isFrozen = false;
    protected double _remainingFrozenTime = 0;
    protected boolean _isFreezy = false;
    protected double _remainingFreezyTime = 0;
    protected double _speed = (double) 1 / 4 * MapBlock.BLOCK_SIZE / GlobalSettings.FPS;
    // ( 1 block / 1 sec) * ( 1 pixel / 1 block ) * ( 1 sec / 1 frame ) = ( pixel / frame )

    public double getDamage() {
        return _damage;
    }


    @JsonIgnore
    protected transient ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();
    @JsonIgnore
    protected transient ArrayList<IEventSubscriber> _eatingEventSubscribers = new ArrayList<>();
    @JsonIgnore
    protected transient ArrayList<IEventSubscriber> _burnEventSubscribers = new ArrayList<>();
    @JsonIgnore
    protected transient ArrayList<IEventSubscriber> _deathEventSubscribers = new ArrayList<>();
    @JsonIgnore
    protected transient ArrayList<IEventSubscriber> _freezyEventSubscribers = new ArrayList<>();
    @JsonIgnore
    protected transient ArrayList<IEventSubscriber> _frozenEventSubscribers = new ArrayList<>();

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

    public void subscribeToFreezyEvent(IEventSubscriber eventSubscriber) {
        this._freezyEventSubscribers.add(eventSubscriber);

    }

    public void subscribeToFrozenEvent(IEventSubscriber eventSubscriber) {
        this._frozenEventSubscribers.add(eventSubscriber);

    }


    @Override
    public void update() {
        if (!_isDisposed) {
            checkCollision();
            if (_state.equals(GeneralZombieState.DEAD)) {
                return;
            }
            if (_isFrozen) {
                if (_remainingFrozenTime < 0) {
                    _isFrozen = false;

                    for (IEventSubscriber eventSubscriber : _frozenEventSubscribers) {
                        eventSubscriber._notify(this);
                    }
                    _remainingFrozenTime = 0;

                } else {
                    _remainingFrozenTime -= 1000.0 / GlobalSettings.FPS;
                }
            } else {
                if (_remainingFreezyTime < 0) {
                    _isFreezy = false;

                    for (IEventSubscriber eventSubscriber : _freezyEventSubscribers) {
                        eventSubscriber._notify(this);
                    }
                    _remainingFreezyTime = 0;

                } else {
                    _remainingFreezyTime = -1000.0 / GlobalSettings.FPS;
                }

                var facingEnemyZombie = _gameEngine.queryHypnotizedZombie(
                        z -> z.getCoordinate().equals(_coordinate)
                );
                if (facingEnemyZombie != null) {
                    eatZombie(facingEnemyZombie);
                    _tick++;
                    return;
                }

                var zombieBlock = _gameEngine.getBlockByCoordinate(this._coordinate);
                if (zombieBlock != null) {
                    if (Math.abs(this._column - zombieBlock.getColumn()) > 1) {
                        var test = 2;
                    }
                    this._column = zombieBlock.getColumn(); // Update Current Block
                    if (zombieBlock.getPlant() != null) {
                        eatPlant(zombieBlock.getPlant());
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

    public void getHitByZombie(HypnotizedZombieGameObject obj) {
        _health -= obj.getDamage();
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

    public void getFreezy(Duration duration) {
        this._isFreezy = true;
        _remainingFreezyTime = duration.toMillis();
        for (IEventSubscriber eventSubscriber : _freezyEventSubscribers) {
            eventSubscriber._notify(this);
        }
    }

    public void getFrozen(Duration duration) {
        _remainingFrozenTime = duration.toMillis();
        this._isFrozen = true;
        for (IEventSubscriber eventSubscriber : _frozenEventSubscribers) {
            eventSubscriber._notify(this);
        }
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

    private void eatPlant(AbstractPlantGameObject plant) {
        if (lastBiteMillis == -1) { // Start eating
            lastBiteMillis = getMilliseconds();
            this._state = GeneralZombieState.EATING;
            for (IEventSubscriber subscriber : _eatingEventSubscribers) {
                subscriber._notify(this);
            }
            if (plant instanceof HypnoShroomGameObject) {
                plant.getHit(0);
                var id = "HypnotizedZombie_" + UUID.randomUUID();
                var hypnotizedZombie = HypnotizedZombieGameObject
                        .createHypnotizedZombieGameObject(_gameEngine, id, this);
                super.dispose(true);
                _gameEngine.spawnObject(hypnotizedZombie);
            }
        }
        if (getMilliseconds() - lastBiteMillis > _biteCoolDown.toMillis()) {
            plant.getHit(this._damage);
            lastBiteMillis = getMilliseconds();
        }
    }

    private void eatZombie(HypnotizedZombieGameObject zombie) {
        if (lastBiteMillis == -1) { // Start eating
            this._state = GeneralZombieState.EATING;
            for (IEventSubscriber subscriber : _eatingEventSubscribers) {
                subscriber._notify(this);
            }
        }
        if (getMilliseconds() - lastBiteMillis > _biteCoolDown.toMillis()) {
            zombie.getHit(this);
            lastBiteMillis = getMilliseconds();
        }
    }

    private double getMilliseconds() {
        return this._tick * 1000.0 / GlobalSettings.FPS;
    }

    // Serialization
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _movementEventSubscribers = new ArrayList<>();
        _eatingEventSubscribers = new ArrayList<>();
        _burnEventSubscribers = new ArrayList<>();
        _deathEventSubscribers = new ArrayList<>();
        _freezyEventSubscribers = new ArrayList<>();
        _frozenEventSubscribers = new ArrayList<>();
    }
}
