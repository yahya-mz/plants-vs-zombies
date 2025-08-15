package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.Plants.ScaredyShroomGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.ScaredyShroomAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ScaredyShroomVisualObject extends AbstractPlantVisualObject {

    private final VisualEngine _engine;

    public ScaredyShroomVisualObject(ScaredyShroomGameObject gameObject, VisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/ScaredyShroom/ScaredyShroom_0.png")));

        gameObject.subscribeToShootingEvent(gameObj -> Platform.runLater(() -> {
//                    var bulletVisualObject = new ShroomBulletVisualObject((ShroomBulletGameObject) gameObject, engine);
//                    _engine.spawnVisualObject(bulletVisualObject);
        }));


        gameObject.subscribeToSwitchFearedEvent(gameObj -> Platform.runLater(() -> {
            changeStateTo(((ScaredyShroomGameObject) gameObj)._state);
        }));

        var temp_this = this;
        gameObject.subscribeToEatenEvent(gameObject1 -> _engine.disposeObject(temp_this));

        gameObject.subscribeToWakeUpEvent(gameObj -> Platform.runLater(() -> {
            changeStateTo(ScaredyShroomGameObject.ScaredyShroomState.STANDING);
        }));

    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(ScaredyShroomAnimations.getFrames((ScaredyShroomAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        changeStateTo(((ScaredyShroomGameObject) _gameObject)._state);
    }


    public ScaredyShroomVisualObject changeStateTo(ScaredyShroomGameObject.ScaredyShroomState state) {
        switch (state) {
            case SLEEPING -> {
                playAnimation(ScaredyShroomAnimations.Animations.SLEEPING, Duration.millis(80));
            }
            case STANDING -> {
                playAnimation(ScaredyShroomAnimations.Animations.STANDING, Duration.millis(80));
            }
            case FEARED -> {
                playAnimation(ScaredyShroomAnimations.Animations.FEARED, Duration.millis(80));
            }
        }
        return this;
    }
}
