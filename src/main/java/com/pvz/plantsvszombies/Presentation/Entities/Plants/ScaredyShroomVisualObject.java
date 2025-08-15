package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.ScaredyShroomGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
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

        gameObject.subscribeToShootingEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
//                    var bulletVisualObject = new ShroomBulletVisualObject((ShroomBulletGameObject) gameObject, engine);
//                    _engine.spawnVisualObject(bulletVisualObject);
                });
            }
        });

//        gameObject.subscribeToBeingEvent(new IEventSubscriber() {//notify//fix this later
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

        gameObject.subscribeToWakeUpEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    changeStateTo(ScaredyShroomGameObject.ScaredyShroomState.STANDING);
                });
            }
        });

    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(ScaredyShroomAnimations.getFrames((ScaredyShroomAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        switch (((ScaredyShroomGameObject) _gameObject)._state) {
            case CRYING -> {
                changeStateTo(ScaredyShroomGameObject.ScaredyShroomState.CRYING);
            }
            case SLEEPING -> {
                changeStateTo(ScaredyShroomGameObject.ScaredyShroomState.SLEEPING);
            }
            case STANDING -> {
                changeStateTo(ScaredyShroomGameObject.ScaredyShroomState.STANDING);
            }
        }
    }


    public ScaredyShroomVisualObject changeStateTo(ScaredyShroomGameObject.ScaredyShroomState state) {
        switch (state) {
            case SLEEPING -> {
                playAnimation(ScaredyShroomAnimations.Animations.SLEEPING, Duration.millis(80));
            }
            case STANDING -> {
                playAnimation(ScaredyShroomAnimations.Animations.STANDING, Duration.millis(80));
            }
            case CRYING -> {
                playAnimation(ScaredyShroomAnimations.Animations.CRYING, Duration.millis(80));
            }
        }
        return this;
    }

//    public ScaredyshroomVisualObject changeStateTo(ScaredyshroomVisualObject.States state) {
//        switch (state) {
//            case SLEEPING -> {
//                _currentState = ScaredyshroomVisualObject.States.SLEEPING;
//                playAnimation(ScaredyshroomAnimations.Animations.SLEEPING, Duration.millis(80));
//            }
//            case STANDING -> {
//                _currentState = Scare.States.STANDING;
//            }
//        }
//        return null;
//    }
}
