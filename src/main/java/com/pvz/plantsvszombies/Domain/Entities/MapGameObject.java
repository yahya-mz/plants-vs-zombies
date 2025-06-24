package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Events.MapSpawnEvent;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;

import java.util.ArrayList;

public class MapGameObject extends AbstractGameObject {

    private final int _rows;
    private final int _columns;

    private boolean isDisposed = false;

    //    AbstractPlantGameObject[] _plants;
    MapBlock[] _blocks;

    private IGameEngine _engine;

    ArrayList<IEventSubscriber> _plantingEventSubscribers = new ArrayList<>();
    ArrayList<IEventSubscriber> _spawningObjectEventSubscribers = new ArrayList<>();

    public static MapGameObject createMapGameObject(int rows, int columns, String id, Coordinate coordinate, IGameEngine engine) {
        return new MapGameObject(rows, columns, id, coordinate, engine);
    }

    private MapGameObject(int rows, int columns, String id, Coordinate coordinate, IGameEngine engine) {
//        this._plants = new AbstractPlantGameObject[_rows * _columns]; Error, might not have been initialized
        this._columns = columns;
        this._rows = rows;
//        this._plants = new AbstractPlantGameObject[_rows * _columns];
        this._blocks = new MapBlock[_rows * _columns];
        this._ID = id;
        this._coordinate = coordinate;
        this._engine = engine;
    }

    public int getRows() {
        return this._rows;
    }

    public int getColumns() {
        return this._columns;
    }

    @Override
    public void spawn() {
//        MapSpawnEvent.emit(this);
    }

    @Override
    public void update() {

    }

    public void initBlocks(MapBlock[] blocks) {
        _blocks = blocks;
    }

    public MapBlock getBlock(int row, int col) {
        return _blocks[_columns * row + col];
    }

    public boolean isOccupied(int row, int column) {
        return (_blocks[_columns * row + column].getPlant() != null);
    }

    public AbstractPlantGameObject getPlantAtBlock(int row, int column) {
        return _blocks[_columns * row + column].getPlant();
    }

    public void plant(AbstractPlantGameObject plant) {//calling visual
        System.out.println("Planting");
        _blocks[plant.getRow() * _rows + plant.getColumn()].setPlant(plant);
        for (IEventSubscriber eventSubscriber : _plantingEventSubscribers) {
            eventSubscriber._notify(plant);
        }
    }

//    public void spawnByCoordinate(AbstractGameObject obj) {
//        for (IEventSubscriber subscriber : _spawningObjectEventSubscribers) {
//            subscriber._notify(obj);
//        }
//    }

    public void subscribeToPlantingEvent(IEventSubscriber subscriber) {
        _plantingEventSubscribers.add(subscriber);
    }

    public void subscribeToSpawningObjectEvent(IEventSubscriber subscriber) {
        _spawningObjectEventSubscribers.add(subscriber);
    }
}
