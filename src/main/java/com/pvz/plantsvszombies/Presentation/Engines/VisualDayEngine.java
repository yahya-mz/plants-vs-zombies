package com.pvz.plantsvszombies.Presentation.Engines;

import com.pvz.plantsvszombies.Domain.Engines.DayEngine;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralTransformAnimation;
import com.pvz.plantsvszombies.Presentation.Entities.*;
import com.pvz.plantsvszombies.Presentation.Entities.Bullets.NormalBulletVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Zombies.*;
import com.pvz.plantsvszombies.Presentation.GUI.Views.AbstractLevelView;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.*;
import com.pvz.plantsvszombies.Presentation.GUI.Views.DayMenu;
import com.pvz.plantsvszombies.Presentation.GUI.Views.DayView;
import com.pvz.plantsvszombies.Presentation.Entities.Zombies.ImpZombieVisualObject;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


import java.util.ArrayList;
import java.util.UUID;

public class VisualDayEngine extends VisualEngine {

    private final ObjectProperty<Class<? extends AbstractPlantVisualObject>> selectedPlantTypeProp =
            new SimpleObjectProperty<>(null);

    public VisualDayEngine(DayView levelStage, DayEngine gameEngine) {
        _levelStage = levelStage;
        _gameEngine = gameEngine;

        _gameEngine.subscribeToGameObjectSpawnEvent(this::spawnGameObject);
        _gameEngine.subscribeToLostEvent(gameObject -> Platform.runLater(() -> {
            playLosingSequence(gameObject.getId());
        }));
        _gameEngine.subscribeToWinEvent(gameObject -> {
            DayMenu.createLostPopup().show();
        });
    }
}