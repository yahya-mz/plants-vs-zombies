package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Events.MapSpawnEvent;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MapGameObject extends AbstractGameObject {

    private final int _rows;
    private final int _columns;

    private boolean isDisposed = false;

    private MapBlock[] _blocks;

    private final IGameEngine _engine;

    ArrayList<IEventSubscriber> _plantingEventSubscribers = new ArrayList<>();
    ArrayList<IEventSubscriber> _spawningObjectEventSubscribers = new ArrayList<>();

    public static MapGameObject createMapGameObject(String id, Coordinate coordinate, IGameEngine engine) {
        return new MapGameObject(id, coordinate, engine);
    }

    private MapGameObject(String id, Coordinate coordinate, IGameEngine engine) {
//        this._plants = new AbstractPlantGameObject[_rows * _columns]; Error, might not have been initialized
        this._engine = engine;
        this._columns = _engine.getColumnsCount();
        this._rows = _engine.getRowsCount();
        this._blocks = new MapBlock[_rows * _columns];
        this._ID = id;
        this._coordinate = coordinate;
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
    public MapBlock getBlockByCoordinate(Coordinate coordinate) {
        for(MapBlock mapBlock:_blocks){
            if (mapBlock.contains(coordinate)){
                return mapBlock;
            }
        }
        return null;
    }

    public void plant(AbstractPlantGameObject plant) {//calling visual
        System.out.println("Planting");
        this._blocks[plant.getRow() * _columns + plant.getColumn()].setPlant(plant);
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
