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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

//public class MapVisualObject extends AbstractVisualObject {
//    MapGameObject _mapObject;
//
//    private final VisualEngine _engine;
//    private final StackPane[][] visualGrid = new StackPane[5][9];
//
//
//    public MapVisualObject(MapGameObject object, VisualEngine engine) {
//        this._mapObject = object;
//        this._engine = engine;
//
////        var gridPane = new GridPane();
////        gridPane.setPrefSize(700, 500); // یا هر ابعاد مناسب
////        gridPane.setStyle("-fx-background-color: lightyellow; -fx-border-color: red; -fx-border-width: 3;");
//
//
//
//
//
////        for (int i = 0; i < 9; i++) {
////            ColumnConstraints colConst = new ColumnConstraints();
////            colConst.setPercentWidth(100.0 / 9);
////            gridPane.getColumnConstraints().add(colConst);
////        }
//
//        int buttonCols = 9;
//        int realCols = buttonCols + (buttonCols - 1); // 17 ستون واقعی (9 دکمه + 8 فاصله)
//
//        for (int i = 0; i < realCols; i++) {
//            if (i % 2 == 0) { // فقط ستون دکمه‌ها
//                ColumnConstraints colConst = new ColumnConstraints();
//                colConst.setPrefWidth(60); // اندازه ثابت دکمه
//                gridPane.getColumnConstraints().add(colConst);
//            } else {
//                // نذار هیچ column constraint روی spacerها باشه!
//                gridPane.getColumnConstraints().add(new ColumnConstraints()); // خالی
//            }
//        }
//
//
//
//
//        for (int i = 0; i < 5; i++) {
//            RowConstraints rowConst = new RowConstraints();
//            rowConst.setPercentHeight(100.0 / 5);
//            gridPane.getRowConstraints().add(rowConst);
//        }
//
//        gridPane.setPadding(new Insets(140, 70, 0, 190));
////        gridPane.setHgap(7);
////        gridPane.setVgap(0);
//        System.out.println("Demo");
//
//        gridPane.setBackground(new Background(
//                new BackgroundImage(
//                        new Image(GlobalSettings.getResource("graphics/Items/Background/daymap.jpg").toString(), true),
//                        BackgroundRepeat.NO_REPEAT,
//                        BackgroundRepeat.NO_REPEAT,
//                        BackgroundPosition.CENTER,
//                        new BackgroundSize(
//                                100, 100,
//                                true, true,
//                                false, true
//                        )
//
//                )
//        ));
//
//        VBox mainContainer = new VBox();
//        mainContainer.setPadding(new Insets(140, 70, 0, 190));
//
//        for (int row = 0; row < 5; row++) {
//            HBox rowBox = new HBox();
//            double customHGap = switch (row) {
//                case 0 -> 5;
//                case 1 -> 10;
//                case 2 -> 20;
//                case 3 -> 40;
//                case 4 -> 60;
//                default -> 0;
//            };
//            rowBox.setSpacing(customHGap);
//            rowBox.setAlignment(Pos.CENTER_LEFT);
//
//            for (int col = 0; col < 9; col++) {
//                Button cellButton = new Button();
//                cellButton.setPrefSize(60, 80);
//                cellButton.setStyle("-fx-background-color: #FF000022; -fx-border-color: black; -fx-border-width: 1;");
//
//                final int r = row;
//                final int c = col;
//                cellButton.setOnMouseClicked(e -> {
//                    if (e.getButton() == MouseButton.PRIMARY) {
//                        System.out.println("Clicked on row " + r + ", column " + c);
//                        cellButton.setFocusTraversable(false);
//                    }
//                });
//
//                rowBox.getChildren().add(cellButton);
//                visualGrid[row][col] = cellButton; // اینجا ذخیره می‌کنیم
//            }
//
//            mainContainer.getChildren().add(rowBox);
//        }
//
//
//        this._node = mainContainer;
//
////        gridPane.setBackground(new Background(
////                new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)
////        ));
//
//        _mapObject.subscribeToPlantingEvent(new IEventSubscriber() {
//            @Override
//            public void _notify(AbstractGameObject gameObject) {//planting visual
//                switch (gameObject) {
//                    case PeashooterGameObject p -> {
//                        var visualObject = new PeashooterVisualObject(p, _engine);
//                        plant(visualObject, p.getRow(), p.getColumn());//calling to plant visual
////                        Platform.runLater(() -> {
////
////                        });
//                    }
//                    default -> {
//                    }
//                }
//            }
//        });
//
//        _mapObject.subscribeToSpawningObjectEvent(new IEventSubscriber() {
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                Platform.runLater(() -> {
//                    if (gameObject instanceof NormalBulletGameObject) {
//                        spawnByCoordinate(new NormalBulletVisualObject((NormalBulletGameObject) gameObject));
//                    }
//                });
//            }
//        });
////        plant(new PeashooterVisualObject(), 1, 1);
//    }
//
//
//    @Override
//    public void spawn() {
//
//    }
//
//    public void plant(AbstractVisualObject object, int row, int column) {//explain this
//        object.getNode().boundsInParentProperty().addListener((observable, newVal, oldVal) -> {///؟؟؟؟؟
////            System.out.println(oldVal.getCenterX());
////            System.out.println(newVal.getCenterX());
//            if (oldVal.getCenterX() == newVal.getCenterX()) {
//                object.spawn();
//            }
//        });
//
//        Node oldNode = visualGrid[row][column];
//        if (oldNode == null) return;
//
//        HBox rowBox = (HBox) ((VBox) this._node).getChildren().get(row);
//        rowBox.getChildren().set(rowBox.getChildren().indexOf(oldNode), object.getNode());
//
//        visualGrid[row][column] = object.getNode();
//    }
//
//
//    public void spawnByCoordinate(AbstractVisualObject object) {
//        _engine.spawnVisualObject(object);
//    }
//}

import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.input.MouseButton;

public class MapVisualObject extends AbstractVisualObject {
    MapGameObject _mapObject;
    private final VisualEngine _engine;
    private final StackPane[][] visualGrid = new StackPane[5][9];

    public MapVisualObject(MapGameObject object, VisualEngine engine) {
        this._mapObject = object;
        this._engine = engine;

        VBox mainContainer = new VBox();
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(140, 70, 0, 190));


        mainContainer.setBackground(new Background(
                new BackgroundImage(
                        new Image(GlobalSettings.getResource("graphics/Items/Background/daymap.jpg").toString(), true),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(100, 100, true, true, false, true)
                )
        ));


        //
        for (int row = 0; row < 5; row++) {
            HBox rowBox = new HBox();

            double customHGap = switch (row) {//فاصله بین هر عنصر در یک ریدف
                case 0 -> 7;
                case 1 -> 7;
                case 2 -> 7;
                case 3 -> 7;
                case 4 -> 7;
                default -> 7;
            };

            double buttonWidth, buttonHeight;
            switch (row) {//custom button size for each row
                case 0 -> {
                    buttonWidth = 84;
                    buttonHeight = 84;
                }
                case 1 -> {
                    buttonWidth = 86;
                    buttonHeight = 86;
                }
                case 2 -> {
                    buttonWidth = 88;
                    buttonHeight = 89;
                }
                case 3 -> {
                    buttonWidth = 89;
                    buttonHeight = 88;
                }
                case 4 -> {
                    buttonWidth = 91;
                    buttonHeight = 91;
                }
                default -> {
                    buttonWidth = 80;
                    buttonHeight = 80;
                }
            }


            Insets rowMargin = switch (row) {//جابه جایی هر ردیف
                case 0 -> new Insets(-13, -10, -10, -10);
                case 1 -> new Insets(5, -10, -10, -20);//up,right,down,left
                case 2 -> new Insets(7, -10, -10, -30);
                case 3 -> new Insets(7, -10, -10, -40);
                case 4 -> new Insets(7, -10, -10, -52);
                default -> Insets.EMPTY;
            };
            VBox.setMargin(rowBox, rowMargin);


            rowBox.setSpacing(customHGap);
            rowBox.setAlignment(Pos.CENTER_LEFT);

            for (int col = 0; col < 9; col++) {
                Button cellButton = new Button();
                cellButton.setPrefSize(buttonWidth, buttonHeight);
                cellButton.setStyle("-fx-background-color: #FF000022; -fx-border-color: black; -fx-border-width: 1;");

                final int r = row;
                final int c = col;
                cellButton.setOnMouseClicked(e -> {
                    if (e.getButton().equals(MouseButton.PRIMARY)) {
                        if (_engine.getSelectedPlantType() != null) {
                            _engine.plant(_engine.getSelectedPlantType(), r, c);
                            _engine.clearSelectedPlantType();
                        } else {
                            System.out.println("no plant have been selected!!!");
                        }
                    }
                });

                StackPane cell = new StackPane();
                cell.getChildren().add(cellButton);
                visualGrid[row][col] = cell;

                rowBox.getChildren().add(cell);
            }

            mainContainer.getChildren().add(rowBox);
        }
        //

        this._node = mainContainer;

        _mapObject.subscribeToPlantingEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                if (gameObject instanceof PeashooterGameObject p) {
                    var visualObject = new PeashooterVisualObject(p, _engine);
                    plant(visualObject, p.getRow(), p.getColumn());
                }
            }
        });

        _mapObject.subscribeToSpawningObjectEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    if (gameObject instanceof NormalBulletGameObject bullet) {
                        spawnByCoordinate(new NormalBulletVisualObject(bullet));
                    }
                });
            }
        });
    }

    @Override
    public void spawn() {
    }

    public void plant(AbstractVisualObject object, int row, int column) {
        object.getNode().boundsInParentProperty().addListener((observable, newVal, oldVal) -> {
            if (oldVal.getCenterX() == newVal.getCenterX()) {
                object.spawn();
            }
        });

        StackPane cell = visualGrid[row][column];
        cell.getChildren().add(object.getNode());
    }

    public void spawnByCoordinate(AbstractVisualObject object) {
        _engine.spawnVisualObject(object);
    }
}


