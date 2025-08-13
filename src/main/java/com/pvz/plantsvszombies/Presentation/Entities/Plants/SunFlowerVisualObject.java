package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.SunFlowerGameObject;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.SunFlowerAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class SunFlowerVisualObject extends AbstractPlantVisualObject {
    public enum States {
        STANDING,
        GLOWING
    }

    private States _currentState;
    private final VisualEngine _engine;

    public SunFlowerVisualObject(SunFlowerGameObject gameObject, VisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        _engine = engine;

        gameObject.subscribeToGlowingEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    changeStateTo(States.GLOWING);
                    //
                    System.out.println("Shooting: " + gameObject.getCoordinate().x() + "," + gameObject.getCoordinate().y());
                    var FlowerSunVisualObject = new FlowerSunVisualObject(((SunGameObject) gameObject),_engine);
                    _engine.spawnVisualObject(FlowerSunVisualObject);
                    //
                });
            }
        });

        var temp_this = this;
        gameObject.subscribeToEatenEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                _engine.disposeObject(temp_this);
            }
        });

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/SunFlower/SunFlower_0.png")));
//        _node.setTranslateY(_visualCoordinate.y());
//        _node.setTranslateX(_visualCoordinate.x());

    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(SunFlowerAnimations.getFrames((SunFlowerAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        Platform.runLater(() -> {
            System.out.println("demodemo");
            playAnimation(SunFlowerAnimations.Animations.STANDING,Duration.millis(90));//standing
            changeStateTo(States.STANDING);
        });
    }

    public SunFlowerVisualObject changeStateTo(States state) {//useless
        switch (state) {
            case STANDING -> {
                _currentState = States.STANDING;
            }
        }
        return null;
    }
}
