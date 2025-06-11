package com.pvz.plantsvszombies.Presentation.Entities;

import javafx.scene.Node;

public abstract class AbstractVisualObject {
    protected Node _node;
    public Node getNode(){
        return this._node;
    }
    public abstract void spawn();
}
