package com.pvz.plantsvszombies.Presentation.Engines;

import com.pvz.plantsvszombies.Domain.Engines.DayEngine;
import com.pvz.plantsvszombies.Presentation.GUI.Views.DayMenu;
import com.pvz.plantsvszombies.Presentation.GUI.Views.DayView;
import javafx.application.Platform;

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