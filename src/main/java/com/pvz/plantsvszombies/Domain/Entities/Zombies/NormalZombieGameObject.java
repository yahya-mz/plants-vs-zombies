package com.pvz.plantsvszombies.Domain.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.AbstractBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;
import com.pvz.plantsvszombies.GlobalSettings;

import java.util.ArrayList;

public class NormalZombieGameObject extends AbstractZombieGameObject {

    private IGameEngine _engine;
    private int tick = 1;
    private boolean _isDisposed = false;

    private enum State {
        MOVING,
        EATING
    }

    private State _state;

    private ArrayList<IEventSubscriber> _deathEventSubscribers = new ArrayList<>();
    private ArrayList<IEventSubscriber> _eatingEventSubscribers = new ArrayList<>();
    private ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();

    public static NormalZombieGameObject createNormalZombieGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new NormalZombieGameObject(gameEngine, id, coordinate, row, column);
    }

    NormalZombieGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._engine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._health = 300;
        this._damage = 25;
        this._speed = 0.9;

        this._state = State.MOVING;
    }

    public void subscribeToDeathEvent(IEventSubscriber eventSubscriber) {
        this._deathEventSubscribers.add(eventSubscriber);
    }

    public void subscribeToEatingEvent(IEventSubscriber eventSubscriber) {
        this._eatingEventSubscribers.add(eventSubscriber);
    }

    public void subscribeToMovementEvent(IEventSubscriber eventSubscriber) {
        this._movementEventSubscribers.add(eventSubscriber);
    }

    @Override
    public void spawn() {

    }

    private double lastBitMillis = -1;

    @Override
    public void update() {
        if (!_isDisposed) {
            var plant = _engine.getPlantAtBlock(this._row, this._column);

            for (AbstractGameObject obj : _engine.getGameObjects()) {
                if (obj instanceof AbstractBulletGameObject) {
                    if (((AbstractBulletGameObject) obj).getRow() == this._row
                            && Math.abs(((AbstractBulletGameObject) obj).getCoordinate().x() - this._coordinate.x()) < (this._speed + ((AbstractBulletGameObject) obj).getSpeed()) / 2) {
                        getHit((AbstractBulletGameObject) obj);
                        ((AbstractBulletGameObject) obj).collide();
                    }
                }
            }
//            System.out.println(this._row + "," + this._column);
            if (plant != null) {
                if (lastBitMillis == -1) {
                    lastBitMillis = getMilliseconds();
                }
                this._state = State.EATING;
                if (getMilliseconds() - lastBitMillis % 500 == 0) {
                    plant.getHit(this._damage);
                    lastBitMillis = getMilliseconds();
                }


            } else {
                this._state = State.MOVING;
                lastBitMillis = -1;

                this._coordinate.traverse(-1 * _speed, 0);
                for (IEventSubscriber subscriber : _movementEventSubscribers) {
                    subscriber._notify(this);
                }
            }
        }
    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public void getHit(AbstractBulletGameObject bullet) {
        if (bullet.getBulletType().equals(AbstractBulletGameObject.BulletType.NORMAL_BULLET)) {
            _health -= bullet.getDamage();
        }
        if (_health <= 0) {
            die();
        }
        System.out.println(_health);
    }

    @Override
    public double getSpeed() {
        return _speed;
    }

    private void die() {
        _isDisposed = true;
        _engine.disposeObject(this);
        this._engine = null;

        for (IEventSubscriber eventSubscriber : _deathEventSubscribers) {
            eventSubscriber._notify(this);
        }
    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

}
