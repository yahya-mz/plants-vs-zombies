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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

public class MapVisualObject extends AbstractVisualObject {
    MapGameObject _mapObject;

    private final VisualEngine _engine;

    public MapVisualObject(MapGameObject object, VisualEngine engine) {
        this._mapObject = object;
        this._engine = engine;

        var gridPane = new GridPane();

        for (int i = 0; i < 9; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / 9);
            gridPane.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < 5; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / 5);
            gridPane.getRowConstraints().add(rowConst);
        }

        gridPane.setPadding(new Insets(140, 70, 0, 190));
        gridPane.setHgap(7);
        gridPane.setVgap(0);
        System.out.println("Demo");
        gridPane.setBackground(new Background(
                new BackgroundImage(
                        new Image(GlobalSettings.getResource("graphics/Items/Background/daymap.jpg").toString(), true),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(
                                100, 100,
                                true, true,
                                false, true
                        )

                )
        ));

        gridPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                Bounds bounds = gridPane.getLayoutBounds();
                double x = e.getX();
                double y = e.getY();

                int column = (int) (x / gridPane.getHgap() + 0.5);
                int row = (int) (y / gridPane.getVgap() + 0.5);

                // شرط: فقط وقتی چیزی انتخاب شده
                if (_engine.getSelectedPlantType() != null) {
                    _engine.plant((Class<? extends AbstractPlantVisualObject>) _engine.getSelectedPlantType(), row, column);
                    _engine.clearSelectedPlantType(); // پاک کردن انتخاب بعد از کاشتن
                }
            }
        });


        this._node = gridPane;

//        gridPane.setBackground(new Background(
//                new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)
//        ));

        _mapObject.subscribeToPlantingEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {//planting visual
                switch (gameObject) {
                    case PeashooterGameObject p -> {
                        var visualObject = new PeashooterVisualObject(p, _engine);
                        plant(visualObject, p.getRow(), p.getColumn());//calling to plant visual
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

    public void plant(AbstractVisualObject object, int row, int column) {//explain this
        object.getNode().boundsInParentProperty().addListener((observable, newVal, oldVal) -> {///؟؟؟؟؟
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
