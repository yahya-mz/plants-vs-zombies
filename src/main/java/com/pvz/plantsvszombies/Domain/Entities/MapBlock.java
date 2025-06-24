package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;

public class MapBlock {
    Coordinate _top_left;
    Coordinate _bottom_right;

    private AbstractPlantGameObject _plant = null;

    public MapBlock(Coordinate top_left, Coordinate bottom_right) {
        this._top_left = top_left;
        this._bottom_right = bottom_right;
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

    public void setPlant(AbstractPlantGameObject plant){
        this._plant = plant;
    }

    public AbstractPlantGameObject getPlant(){
        if (_plant == null){
            return null;
        }
        return _plant;
    }
}