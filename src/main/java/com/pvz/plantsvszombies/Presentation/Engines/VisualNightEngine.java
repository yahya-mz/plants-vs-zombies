package com.pvz.plantsvszombies.Presentation.Engines;

import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Engines.NightEngine;
import com.pvz.plantsvszombies.Presentation.GUI.Views.NightView;
import javafx.application.Platform;

public class VisualNightEngine extends VisualEngine {


    public VisualNightEngine(NightView levelStage, NightEngine gameEngine) {
        _levelStage = levelStage;
        _gameEngine = gameEngine;

        _gameEngine.subscribeToGameObjectSpawnEvent(this::spawnGameObject);
        _gameEngine.subscribeToLostEvent(gameObject -> Platform.runLater(() -> {
            playLosingSequence(gameObject.getId());
        }));
        _gameEngine.subscribeToWinEvent(gameObject -> {

        });
    }

}