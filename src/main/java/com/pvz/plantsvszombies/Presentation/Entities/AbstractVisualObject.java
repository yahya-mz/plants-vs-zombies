package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import javafx.scene.Node;

public abstract class AbstractVisualObject {
    protected Coordinate _visualCoordinate;

    protected Node _node;
    public Node getNode(){
        return this._node;
    }
    public abstract void spawn();

}
