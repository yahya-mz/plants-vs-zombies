package com.pvz.plantsvszombies.Domain.Entities.Plants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

public class TallNutGameObject extends AbstractPlantGameObject implements Serializable {
    private int tick = 1;

    private transient ArrayList<IEventSubscriber> _cracked_1_EventSubscribers = new ArrayList<>();
    private transient ArrayList<IEventSubscriber> _cracked_2_EventSubscribers = new ArrayList<>();
    private transient ArrayList<IEventSubscriber> _eatenEventSubscribers = new ArrayList<>();


    public static TallNutGameObject createTallNutGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new TallNutGameObject(gameEngine, id, coordinate, row, column);
    }
    TallNutGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 50;
        this._health = 400;
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
        if (_health == 250) {
            for (IEventSubscriber eventSubscriber : _cracked_1_EventSubscribers) {
                eventSubscriber._notify(this);
            }
        } else if (_health == 125) {
            for (IEventSubscriber eventSubscriber : _cracked_2_EventSubscribers) {
                eventSubscriber._notify(this);
            }
        } else if (_health <= 0) {
            eaten();
        }
    }

    private void eaten() {
        for (IEventSubscriber eventSubscriber : _eatenEventSubscribers) {
            eventSubscriber._notify(this);
        }
        super.dispose();
    }
    private double getMilliseconds() {
        return this.tick * 1000.0 / GlobalSettings.FPS;
    }

    // Serialization
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _cracked_1_EventSubscribers = new ArrayList<>();
        _cracked_2_EventSubscribers = new ArrayList<>();
        _eatenEventSubscribers = new ArrayList<>();
    }
}
