package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.scene.Node;

public abstract class AbstractVisualObject {
    protected Coordinate _visualCoordinate;
    protected AbstractGameObject _gameObject;

    protected Node _node;

    public Node getNode() {
        return this._node;
    }
    public AbstractGameObject getGameObject() {
        return _gameObject;
    }

    public abstract void spawn();
}
