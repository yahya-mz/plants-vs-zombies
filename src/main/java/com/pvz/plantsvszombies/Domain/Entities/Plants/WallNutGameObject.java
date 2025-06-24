package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;
import com.pvz.plantsvszombies.GlobalSettings;

import java.util.ArrayList;

public class WallNutGameObject extends AbstractPlantGameObject {
    private IGameEngine _engine;
    private int tick = 1;
    private boolean _isDisposed = false;

    private ArrayList<IEventSubscriber> _cracked_1_EventSubscribers = new ArrayList<>();
    private ArrayList<IEventSubscriber> _cracked_2_EventSubscribers = new ArrayList<>();
    private ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();


    public static WallNutGameObject createWallNutGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new WallNutGameObject(gameEngine, id, coordinate, row, column);
    }

    WallNutGameObject(IGameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._engine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 50;
        this._health = 750;
    }

    public void subscribeToCracked_1_Event(IEventSubscriber event) {
        this._cracked_1_EventSubscribers.add(event);
    }

    public void subscribeToCracked_2_Event(IEventSubscriber event) {
        this._cracked_2_EventSubscribers.add(event);
    }

    public void subscribeToEatenEvent(IEventSubscriber event) {
        this._eatenEventSubscribers.add(event);
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        tick++;
    }

    @Override
    public void getHit(int damage) {
        _health -= damage;
        if (_health > 0) {
            if (_health < 500 && _health > 250) {
                for (IEventSubscriber eventSubscriber : _cracked_1_EventSubscribers) {
                    eventSubscriber._notify(this);
                }
            } else if (_health < 250) {
                for (IEventSubscriber eventSubscriber : _cracked_2_EventSubscribers) {
                    eventSubscriber._notify(this);
                }
            }
        } else {
            this.eaten();
        }
    }

    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

    private void eaten() {
        this._isDisposed = true;
        this._engine.disposeObject(this);
        this._engine = null;

        for (IEventSubscriber eventSubscriber : _eatenEventSubscribers) {
            eventSubscriber._notify(this);
        }
    }
}
