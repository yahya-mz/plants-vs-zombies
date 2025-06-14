package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class MapVisualObject extends AbstractVisualObject {
    MapGameObject _mapObject;

    private final VisualEngine _engine;

    public MapVisualObject(MapGameObject object, VisualEngine engine) {
        this._mapObject = object;
        this._engine = engine;

        var gridPane = new GridPane();
        gridPane.setPadding(new Insets(140, 0, 0, 190));
        gridPane.setHgap(7);
        gridPane.setVgap(10);
        System.out.println("Demo");
        gridPane.setBackground(new Background(
                new BackgroundImage(
                        new Image(GlobalSettings.getResource("graphics/Items/Background/Background_0.jpg").toString()),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        BackgroundSize.DEFAULT)
        ));
        this._node = gridPane;

//        gridPane.setBackground(new Background(
//                new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)
//        ));
        _mapObject.subscribeToPlantingEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                switch (gameObject) {
                    case PeashooterGameObject p -> {
                        var visualObject = new PeashooterVisualObject(p, _engine);
                        plant(visualObject, p.getRow(), p.getColumn());
//                        Platform.runLater(() -> {
//
//                        });
                    }
                    default -> {
                    }
                }
            }
        });
        _mapObject.subscribeToSpawningObjectEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    if (gameObject instanceof NormalBulletGameObject) {
                        spawnByCoordinate(new NormalBulletVisualObject((NormalBulletGameObject) gameObject));
                    }
                });
            }
        });
//        plant(new PeashooterVisualObject(), 1, 1);
    }

    @Override
    public void spawn() {

    }

    public void plant(AbstractVisualObject object, int row, int column) {
        object.getNode().boundsInParentProperty().addListener((observable, newVal, oldVal) -> {
//            System.out.println(oldVal.getCenterX());
//            System.out.println(newVal.getCenterX());
            if (oldVal.getCenterX() == newVal.getCenterX()) {
                object.spawn();
            }
        });
        ((GridPane) this._node).add(object.getNode(), row, column);
    }

    public void spawnByCoordinate(AbstractVisualObject object) {
        _engine.spawnVisualObject(object);
    }
}
