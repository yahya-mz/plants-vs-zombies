package com.pvz.plantsvszombies.Presentation;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.*;
import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.GameEngine.DayEngine;
import com.pvz.plantsvszombies.Presentation.Entities.*;
import com.pvz.plantsvszombies.GUI.Views.AbstractLevelView;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class VisualEngine {
    private final int _width = 1280;
    private final int _height = 728;


    private final ArrayList<AbstractVisualObject> _visualObjects = new ArrayList<>();
    private AbstractLevelView _levelStage;

    private final IGameEngine _gameEngine;

    public VisualEngine(AbstractLevelView levelStage, IGameEngine gameEngine) {
        _levelStage = levelStage;
        _gameEngine = gameEngine;

        var temp_this = this;
        if (_gameEngine instanceof DayEngine) {
            ((DayEngine) _gameEngine).subscribeToMapSpawnEvent(new IEventSubscriber() {
                @Override
                public void _notify(AbstractGameObject gameObject) {
                    MapVisualObject object = new MapVisualObject((MapGameObject) gameObject, temp_this);
                    spawnVisualObject(object, 1);
                }
            });

            ((DayEngine) _gameEngine).subscribeToSkySunSpawnEvent(new IEventSubscriber() {
                @Override
                public void _notify(AbstractGameObject gameObject) {
                    SkySunVisualObject object = new SkySunVisualObject((SunGameObject) gameObject, temp_this);
                    spawnVisualObject(object);
                }
            });
        }
    }

    public int getWidth() {
        return this._width;
    }

    public int getHeight() {
        return this._height;
    }

    public void disposeObject(AbstractVisualObject obj) {
        ((Pane) obj.getNode().getParent()).getChildren().remove(obj.getNode());
        _visualObjects.remove(obj);
//        _levelStage.getScene().getRoot().getChildrenUnmodifiable().remove(obj.getNode());
    }

    public void spawnVisualObject(AbstractVisualObject object) {
        Platform.runLater(() -> {
            _levelStage.getGameBoxPane().getChildren().add(object.getNode());
            object.spawn();
            this._visualObjects.add(object);
        });
    }

    public void spawnVisualObject(AbstractVisualObject object, int top_z_index) {
        Platform.runLater(() -> {
            _levelStage.getGameBoxPane().getChildren().add(object.getNode());
            for (int i = 0; i < top_z_index; i++) {
                object.getNode().toBack();
            }
            object.spawn();
            this._visualObjects.add(object);
        });
    }

    public void plant(Class<? extends AbstractPlantVisualObject> plantType, int x, int y) {
        Platform.runLater(() -> {
            if (plantType == PeashooterVisualObject.class) {
                String PeashooterObjectId = "Peashooter" + UUID.randomUUID();
                Coordinate coordinate2 = new Coordinate(-1280.0 / 2, 728.0 / 2);
                var obj = PeashooterGameObject.createPeashooterGameObject(this._gameEngine, PeashooterObjectId, coordinate2, x, y);
                try {
                    this._gameEngine.plantObject(obj);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    public AbstractLevelView getLevelStage() {
        return this._levelStage;
    }

}
