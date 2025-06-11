package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;

import java.util.ArrayList;

public class MapGameObject implements IGameObject {

    private int _rows;
    private int _columns;

    ArrayList<AbstractPlant> _plants;

    private String _Id;

    public MapGameObject() {
        this._plants = new ArrayList<>();
    }

    @Override
    public String getId() {
        return this._Id;
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
            if (plant.column == column && plant.row == row) {
                return false;
            }
        }
        return true;
    }

    public void plant(AbstractPlant plant) {
        if (!isOccupied(plant.row, plant.column))
            this._plants.add(plant);

    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {

    }
}
