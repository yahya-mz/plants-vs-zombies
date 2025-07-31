package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.ScaredyShroomGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.ScaredyshroomAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ScaredyshroomVisualObject extends AbstractPlantVisualObject {
    public enum States {
        STANDING,
        CRYING,
        SLEEPING
    }
    private ScaredyshroomVisualObject.States _currentState = States.STANDING;


    private final IVisualEngine _engine;

    public ScaredyshroomVisualObject(ScaredyShroomGameObject gameObject, IVisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/ScaredyShroom/ScaredyShroom_0.png")));

//        gameObject.subscribeToShootingEvent(new IEventSubscriber() {//notify
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                Platform.runLater(() -> {
//                    _currentState = States.STANDING;
//                    System.out.println("Shooting: " + gameObject.getCoordinate().x() + "," + gameObject.getCoordinate().y());
//                    var bulletVisualObject = new ShroomBulletVisualObject((ShroomBulletGameObject) gameObject, engine);
//                    _engine.spawnVisualObject(bulletVisualObject);
//                });
//            }
//        });
//
//        gameObject.subscribeToStopShootingEvent(new IEventSubscriber() {//notify//fix this later
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                Platform.runLater(() -> {
//                    changeStateTo(States.HIDING);
//                    System.out.println("Shooting: " + gameObject.getCoordinate().x() + "," + gameObject.getCoordinate().y());
//                    var bulletVisualObject = new ShroomBulletVisualObject((ShroomBulletGameObject) gameObject, engine);
//                    _engine.spawnVisualObject(bulletVisualObject);
//                });
//            }
//        });
//        gameObject.subscribeToSleepingEvent(new IEventSubscriber() {//notify//fix this later
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                Platform.runLater(() -> {
//                    changeStateTo(States.SLEEPING);
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

    @Override
    public void spawn() {
        if(_currentState.equals(States.STANDING)) {
            playAnimation(ScaredyshroomAnimations.Animations.STANDING, Duration.millis(20));//standing
        } else if (_currentState.equals(States.SLEEPING)) {
            playAnimation(ScaredyshroomAnimations.Animations.SLEEPING, Duration.millis(20));//standing
        }
    }

    public ScaredyshroomVisualObject changeStateTo(ScaredyshroomVisualObject.States state) {
        switch (state) {
            case SLEEPING -> {
                _currentState = States.SLEEPING;
            }
            case STANDING -> {
                _currentState = States.STANDING;
                stopAnimation();
                playAnimation(ScaredyshroomAnimations.Animations.STANDING, Duration.millis(80));
            }
            case CRYING -> {
                if (_currentState.equals(States.STANDING)) {
                    _currentState = States.CRYING;
                    stopAnimation();
                    playAnimation(ScaredyshroomAnimations.Animations.CRYING, Duration.millis(80));
                }
            }
        }
        return null;
    }
}
