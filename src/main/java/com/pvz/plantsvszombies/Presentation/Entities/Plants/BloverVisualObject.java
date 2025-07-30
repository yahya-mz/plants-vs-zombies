package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
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


    private final IVisualEngine _engine;

    public BloverVisualObject(PeashooterGameObject gameObject, IVisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Blover/Blover_0.png")));

//        gameObject.subscribeToBlowingEvent(new IEventSubscriber() {//notify
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                Platform.runLater(() -> {
//                    _currentState = States.BLOWING;
//                });
//            }
//        });

        var temp_this = this;
        gameObject.subscribeToEatenEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                _engine.disposeObject(temp_this);
            }
        });

    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(ScaredyshroomAnimations.getFrames((ScaredyshroomAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(JalapenoAnimations.getFrames((JalapenoAnimations.Animations) animation), frameDuration, cycleCount);

    }

    @Override
    public void spawn() {
        playAnimation(PuffshroomAnimations.Animations.STANDING, Duration.millis(20));//standing
    }

    public BloverVisualObject changeStateTo(BloverVisualObject.States state) {
        switch (state) {
            case STANDING -> {
                _currentState = BloverVisualObject.States.STANDING;
                playAnimation(ScaredyshroomAnimations.Animations.STANDING, Duration.millis(80));
            }
            case BLOWING -> {
                if (_currentState.equals(States.STANDING)) {
                    _currentState = States.BLOWING;
                    stopAnimation();
                    playAnimation(BloverAnimations.Animations.BLOWING, Duration.millis(80), 1);
                    setOnAnimationFinished(e -> changeStateTo(States.STANDING));
                }
            }
        }
        return null;
    }
}

