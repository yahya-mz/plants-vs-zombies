package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Events.SkySunSpawnEvent;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SunGameObject extends AbstractGameObject {
    private final double _points = 50;
    private final int _timeOutMilliseconds = 5000;

    private IGameEngine _engine;

    private boolean isDisposed = false;

    private ArrayList<IEventSubscriber> _timeOutSubscribers = new ArrayList<>();

    public static SunGameObject createSunGameObject(IGameEngine gameEngine, String id, Coordinate coordinate) {
        return new SunGameObject(gameEngine, id, coordinate);
    }

    SunGameObject(IGameEngine gameEngine, String id, Coordinate coordinate) {
        this._engine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
    }
    @Override
    public void spawn() {
        var temp_this = this;
        Timer timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (!isDisposed) {
                            for (IEventSubscriber subscriber : _timeOutSubscribers) {
                                subscriber._notify(temp_this);
                            }
                            isDisposed = true;
                        }
                    }
                },
                _timeOutMilliseconds
        );
    }

    @Override
    public void update() {

    }

    public void gain() {
        _engine.addPoint(this._points);
        _engine.disposeObject(this);
        this.isDisposed = true;
        System.out.println(_engine.getPoint());

    }

    public void dispose() {
        _engine.disposeObject(this);
    }

    public void subscribeToTimeOut(IEventSubscriber eventSubscriber) {
        _timeOutSubscribers.add(eventSubscriber);
    }


}
