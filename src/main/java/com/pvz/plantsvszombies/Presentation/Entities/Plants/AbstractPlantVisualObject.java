package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractAnimatedVisualObject;

public abstract class AbstractPlantVisualObject extends AbstractAnimatedVisualObject {
    protected AbstractPlantGameObject _gameObject;

    public int getRow() {
        return _gameObject.getRow();
    }

    public int getColumn() {
        return _gameObject.getColumn();
    }

    public AbstractPlantGameObject getGameObject() {
        return _gameObject;
    }
}

