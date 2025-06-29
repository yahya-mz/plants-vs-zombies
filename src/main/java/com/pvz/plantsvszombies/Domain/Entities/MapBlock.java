package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;

public class MapBlock {

    private final int _row;
    private final int _column;

    private final Coordinate _top_left;
    private final Coordinate _bottom_right;

    private AbstractPlantGameObject _plant;

    public MapBlock(Coordinate top_left, Coordinate bottom_right, int row, int _column) {
        this._top_left = top_left;
        this._bottom_right = bottom_right;
        this._row = row;
        this._column = _column;
    }

    public Coordinate getBottomRightCoordinate() {
        return _bottom_right;
    }

    public Coordinate getTopLeftCoordinate() {
        return _top_left;
    }

    public Coordinate getCenterCoordinate() {
        return new Coordinate((_top_left.x() + _bottom_right.x()) / 2.0, (_top_left.y() + _bottom_right.y()) / 2.0);
    }
    public int getRow() {return this._row;}
    public int getColumn() {return this._column;}

    public boolean contains(Coordinate coordinate) {
        return
                coordinate.x() >= _top_left.x()
                        && coordinate.x() <= _bottom_right.x()
                        && coordinate.y() >= _top_left.y()
                        && coordinate.y() <= _bottom_right.y();
    }

    public void setPlant(AbstractPlantGameObject plant) {
        this._plant = plant;
    }

    public AbstractPlantGameObject getPlant() {
        return _plant;
    }

}