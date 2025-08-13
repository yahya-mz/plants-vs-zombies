package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class MapGameObject extends AbstractGameObject implements Serializable {

    private final int _rows;
    private final int _columns;

    private boolean isDisposed = false;

    private MapBlock[] _blocks;

    private transient final GameEngine _engine;

    public boolean isBlocksInstantiated = false;

    private transient ArrayList<IEventSubscriber> _blocksReadyEventSubscribers = new ArrayList<>();

    public static MapGameObject createMapGameObject(String id, Coordinate coordinate, GameEngine engine) {
        return new MapGameObject(id, coordinate, engine);
    }

    private MapGameObject(String id, Coordinate coordinate, GameEngine engine) {
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

    public boolean isBlocksInstantiated() {
        return isBlocksInstantiated;
    }

    public void initBlocks(MapBlock[] blocks) {
        _blocks = blocks;
        isBlocksInstantiated = true;

        for (IEventSubscriber eventSubscriber : _blocksReadyEventSubscribers) {
            eventSubscriber._notify(this);
        }
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
        for (MapBlock mapBlock : _blocks) {
            if (mapBlock.contains(coordinate)) {
                return mapBlock;
            }
        }
        return null;
    }

    public void plant(AbstractPlantGameObject plant) {//calling visual
        this._blocks[plant.getRow() * _columns + plant.getColumn()].setPlant(plant);
    }

    public void subscribeToBlocksReadyEvent(IEventSubscriber eventSubscriber) {
        _blocksReadyEventSubscribers.add(eventSubscriber);
    }

    // Serialization
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _blocksReadyEventSubscribers = new ArrayList<>();
    }
}
