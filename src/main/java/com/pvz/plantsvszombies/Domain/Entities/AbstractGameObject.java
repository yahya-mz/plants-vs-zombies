package com.pvz.plantsvszombies.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Interfaces.IDisposable;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class AbstractGameObject implements IDisposable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected String _ID;
    protected Coordinate _coordinate;

    protected transient GameEngine _gameEngine;

    protected boolean _isDisposed = false;

    private transient ArrayList<IEventSubscriber> _disposeEventSubscribers = new ArrayList<>();


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
        dispose(false);
    }

    public void dispose(boolean trigListeners) {
        _gameEngine.disposeObject(this);
        _isDisposed = true;
        if (trigListeners) {
            for (IEventSubscriber _subscriber : _disposeEventSubscribers) {
                _subscriber._notify(this);
            }
        }
    }

    // Serialization
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        _disposeEventSubscribers = new ArrayList<>();
    }
}
