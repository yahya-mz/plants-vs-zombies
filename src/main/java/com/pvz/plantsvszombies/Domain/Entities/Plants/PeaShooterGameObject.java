package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;

import java.util.ArrayList;

public class PeaShooterGameObject extends AbstractPlant {
    private final String _ID;
    private final Coordinate _coordinate;
    private IGameEngine _engine;

    private boolean isDisposed = false;

    private ArrayList<IEventSubscriber> _shootingEventSubscribers = new ArrayList<>();
    private ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();


    public static PeaShooterGameObject createSunGameObject(IGameEngine gameEngine, String id, Coordinate coordinate) {
        return new PeaShooterGameObject(gameEngine, id, coordinate);
    }

    PeaShooterGameObject(IGameEngine gameEngine, String id, Coordinate coordinate) {
        this._engine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
    }

    private void shoot() {
        for (IEventSubscriber eventSubscriber : _shootingEventSubscribers) {
            eventSubscriber._notify(this);
        }
    }

    private void eaten() {
        for (IEventSubscriber eventSubscriber : _eatenEventSubscribers) {
            eventSubscriber._notify(this);
        }
    }

    public void subscribeToShootingEvent(IEventSubscriber event) {
        this._shootingEventSubscribers.add(event);
    }

    public void subscribeToEatenEvent(IEventSubscriber event) {
        this._eatenEventSubscribers.add(event);
    }


    @Override
    public String getId() {
        return this._ID;
    }

    @Override
    public Coordinate getCoordinate() {
        return this._coordinate;
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {

    }
}
