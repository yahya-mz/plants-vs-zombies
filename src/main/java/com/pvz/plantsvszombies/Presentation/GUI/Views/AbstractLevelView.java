package com.pvz.plantsvszombies.Presentation.GUI.Views;

import javafx.scene.layout.*;
import javafx.stage.Stage;

public abstract class AbstractLevelView extends Stage {
    Pane _gameBoxPane;

    public Pane getGameBoxPane() {
        return this._gameBoxPane;
    }

    public abstract void setIsShovelMode(boolean value);
}
