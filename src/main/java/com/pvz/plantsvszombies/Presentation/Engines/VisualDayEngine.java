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
import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.UUID;

public class VisualDayEngine implements IVisualEngine {
    private final int _width = 1280;
    private final int _height = 728;
    //
    private Class<? extends AbstractPlantVisualObject> selectedPlantType;
    //

    private final ArrayList<AbstractVisualObject> _visualObjects = new ArrayList<>();
    private MapVisualObject _currentMapVisualObject;
    private final DayView _levelStage;

public class VisualDayEngine extends VisualEngine {

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