package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Events.MapSpawnEvent;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlant;

import java.util.ArrayList;

public class MapGameObject implements IGameObject {

    private final int _rows;
    private final int _columns;

    private final String _ID;
    private final Coordinate _coordinate;


    private boolean isDisposed = false;

    ArrayList<AbstractPlant> _plants;

    public static MapGameObject createMapGameObject(int rows, int columns, String id, Coordinate coordinate) {
        return new MapGameObject(rows, columns, id, coordinate);
    }

    private MapGameObject(int rows, int columns, String id, Coordinate coordinate) {
        this._plants = new ArrayList<>();
        this._columns = columns;
        this._rows = rows;
        this._ID = id;
        this._coordinate = coordinate;
    }

    @Override
    public String getId() {
        return this._ID;
    }

    @Override
    public Coordinate getCoordinate() {
        return null;
    }

    public int getRows() {
        return this._rows;
    }

    public int getColumns() {
        return this._columns;
    }

    public boolean isOccupied(int row, int column) {
        for (AbstractPlant plant : _plants) {
            if (plant.getColumn() == column && plant.getRow() == row) {
                return false;
            }
        }
        return true;
    }

    public void plant(AbstractPlant plant) {
        if (!isOccupied(plant.getRow(), plant.getColumn()))
            this._plants.add(plant);

    }

    @Override
    public void spawn() {
        MapSpawnEvent.emit(this);
    }

    @Override
    public void update() {

    }

    private ArrayList<IEventSubscriber> _timeOutSubscribers = new ArrayList<>();

    public void subscribeToTimeOut(IEventSubscriber eventSubscriber) {
        _timeOutSubscribers.add(eventSubscriber);
    }
}
