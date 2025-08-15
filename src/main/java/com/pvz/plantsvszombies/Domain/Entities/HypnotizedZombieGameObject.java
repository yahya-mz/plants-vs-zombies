package com.pvz.plantsvszombies.Domain.Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;

import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

public class HypnotizedZombieGameObject extends AbstractGameObject implements Serializable {

    public enum HypnotizedZombieStates {
        MOVING_FORWARD,
        EATING,
        DYING,
    }

    protected int _tick;

    protected int _row;
    protected int _column;

    protected double _health;
    protected int _damage = 25;
    protected Duration _biteCoolDown = Duration.ofMillis(500);
    protected double _speed = (double) 1 / 4 * MapBlock.BLOCK_SIZE / GlobalSettings.FPS;
    // ( 1 block / 1 sec) * ( 1 pixel / 1 block ) * ( 1 sec / 1 frame ) = ( pixel / frame )

    protected final AbstractZombieGameObject.ZombieType _zombieType;
    protected HypnotizedZombieStates _currentState = HypnotizedZombieStates.MOVING_FORWARD;


    protected transient ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();

    protected transient ArrayList<IEventSubscriber> _eatingEventSubscribers = new ArrayList<>();

    protected transient ArrayList<IEventSubscriber> _deathEventSubscribers = new ArrayList<>();

    public static HypnotizedZombieGameObject createHypnotizedZombieGameObject(GameEngine gameEngine, String id, AbstractZombieGameObject zombie) {
        return new HypnotizedZombieGameObject(gameEngine, id, zombie);
    }

    private HypnotizedZombieGameObject(GameEngine gameEngine, String id, AbstractZombieGameObject zombie) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = zombie.getCoordinate().copy();
        this._row = zombie.getRow();
        this._column = zombie.getColumn();
        this._health = 1000;
        _zombieType = zombie.getZombieType();
    }

    public int getDamage() {
        return _damage;
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

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        if (!_isDisposed) {
            var opponentZombie = _gameEngine.getZombiesByRow(this._row).stream()
                    .filter(z -> z.getCoordinate().x() == _coordinate.x()).findFirst().orElse(null);
            if (opponentZombie != null) {
                eat(opponentZombie);
                _tick++;
                return;
            }
            lastBiteMillis = -1;
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

    public HypnotizedZombieStates getCurrentState() {
        return _currentState;
    }

    public AbstractZombieGameObject.ZombieType getZombieType() {
        return _zombieType;
    }

    public void getHit(AbstractZombieGameObject zombie) {
        _health -= zombie.getDamage();
        if (_health <= 0) {
            die();
        }
    }

    private void move() {
        this._coordinate.traverse(_speed, 0);
        for (IEventSubscriber subscriber : _movementEventSubscribers) {
            subscriber._notify(this);
        }
        this._currentState = HypnotizedZombieStates.MOVING_FORWARD;

    }

    private void die() {
//        this._gameEngine = null; Believe it or not but this makes the _engine property null for all the existing ZombieGameObjects in the App
        _currentState = HypnotizedZombieStates.DYING;
        for (IEventSubscriber eventSubscriber : _deathEventSubscribers) {
            eventSubscriber._notify(this);
        }
        super.dispose(true);

    }

    private double lastBiteMillis = -1;

    private void eat(AbstractZombieGameObject opponentZombie) {
        if (lastBiteMillis == -1) { // Start eating
            for (IEventSubscriber subscriber : _eatingEventSubscribers) {
                subscriber._notify(this);
            }
            this._currentState = HypnotizedZombieStates.EATING;
        }
        if (getMilliseconds() - lastBiteMillis > _biteCoolDown.toMillis()) {
            opponentZombie.getHitByZombie(this);
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
        _deathEventSubscribers = new ArrayList<>();
    }
}
