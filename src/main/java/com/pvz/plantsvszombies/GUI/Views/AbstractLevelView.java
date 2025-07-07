package com.pvz.plantsvszombies.GUI.Views;

import javafx.scene.layout.*;
import javafx.stage.Stage;

public abstract class AbstractLevelView extends Stage {
    Pane _gameBoxPane;

    public Pane getGameBoxPane() {
        return this._gameBoxPane;
    }
}
