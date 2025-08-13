package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.BloverGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.CherryBombGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class BloverVisualObject extends AbstractPlantVisualObject {
    public enum States {
        STANDING,
        BLOWING
    }

    private BloverVisualObject.States _currentState = BloverVisualObject.States.STANDING;


    private final VisualEngine _engine;

    public BloverVisualObject(BloverGameObject gameObject, VisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Blover/Blover_0.png")));
        ((ImageView) _node).setFitWidth(85);
        ((ImageView) _node).setFitHeight(85);

        var temp_this = this;
        gameObject.subscribeToBlowingEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    _engine.disposeObject(temp_this);
                });
            }
        });
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(BloverAnimations.getFrames((BloverAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(BloverAnimations.getFrames((BloverAnimations.Animations) animation), frameDuration, cycleCount);

    }

    @Override
    public void spawn() {
        Platform.runLater(() -> {
            _gameObject.spawn();
            var framesCount = BloverAnimations.getFrames(BloverAnimations.Animations.BLOWING).length;
            playAnimation(BloverAnimations.Animations.BLOWING, Duration.millis(CherryBombGameObject.EXPLOSION_TIME.toMillis()).divide(framesCount), 1);//standing//the animation is not inf is one time only
        });
    }
}

