package com.pvz.plantsvszombies.Presentation;

import com.pvz.plantsvszombies.Domain.Entities.IGameObject;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractVisualObject;
import com.pvz.plantsvszombies.Presentation.Views.AbstractLevelView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class VisualEngine {
    private static VisualEngine _instance;

    private final ArrayList<AbstractVisualObject> _visualObjects = new ArrayList<>();
    private AbstractLevelView _levelStage;

    public static VisualEngine init(AbstractLevelView levelStage) {
        _instance = new VisualEngine();
        _instance._levelStage = levelStage;
        return _instance;
    }

    public static VisualEngine getInstance() {
//        if (_instance == null) {
//            throw new Exception("Visual Engine has not been initialized");
//        }
        return _instance;
    }

    public void disposeObject(AbstractVisualObject obj) {
        ((Pane) obj.getNode().getParent()).getChildren().remove(obj.getNode());
        _visualObjects.remove(obj);
//        _levelStage.getScene().getRoot().getChildrenUnmodifiable().remove(obj.getNode());
    }

    public AbstractLevelView getLevelStage() {
        return this._levelStage;
    }

}
