package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Interfaces.IDisposable;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;

import java.util.ArrayList;

public abstract class AbstractGameObject implements IDisposable {

    protected String _ID;
    protected Coordinate _coordinate;
    protected GameEngine _gameEngine;
    protected boolean _isDisposed = false;
    private final ArrayList<IEventSubscriber> _disposeEventSubscribers = new ArrayList<>();


    public String getId() {
        return this._ID;
    }

    public Coordinate getCoordinate() {
        return this._coordinate;
    }

    public abstract void spawn();

    public abstract void update();

    public void subscribeToDisposeEvent(IEventSubscriber eventSubscriber) {
        _disposeEventSubscribers.add(eventSubscriber);
    }

    @Override
    public void dispose() {
        _gameEngine.disposeObject(this);
        _isDisposed = true;
        for (IEventSubscriber _subscriber : _disposeEventSubscribers) {
            _subscriber._notify(this);
        }
    }
}
