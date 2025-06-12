package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class MapVisualObject extends AbstractVisualObject {
    MapGameObject _mapObject;

    public MapVisualObject(MapGameObject object) {
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
//        gridPane.setBackground(new Background(
//                new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)
//        ));
        this._node = gridPane;
        this._mapObject = object;
        plant();
    }

    @Override
    public void spawn() {

    }

    public void plant() {
        var testPlant = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Peashooter/Peashooter_0.png")));
        var testPlant2 = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Peashooter/Peashooter_0.png")));
        var testPlant3 = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Peashooter/Peashooter_0.png")));
        var testPlant4 = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Peashooter/Peashooter_0.png")));
        ((GridPane) this._node).add(testPlant, 1, 1);
        ((GridPane) this._node).add(testPlant2, 2, 1);
        ((GridPane) this._node).add(testPlant3, 1, 2);
        ((GridPane) this._node).add(testPlant4, 2, 2);
    }
}
