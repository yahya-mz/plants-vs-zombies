package com.pvz.plantsvszombies.Presentation.Views;

import com.pvz.plantsvszombies.Presentation.Entities.AbstractVisualObject;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public abstract class AbstractLevelView extends Stage {
    Pane gameBoxPane;

    public void spawnVisualObject(AbstractVisualObject object) {
        gameBoxPane.getChildren().add(object.getNode());
        object.spawn();
    }
}
