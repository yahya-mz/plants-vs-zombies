package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PuffShroomGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.PuffshroomAnimations;
import com.pvz.plantsvszombies.Presentation.Animations.ScaredyShroomAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import com.pvz.plantsvszombies.Presentation.Engines.VisualNightEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class PuffshroomVisualObject extends AbstractPlantVisualObject {
    public enum States {
        STANDING,
        SLEEPING
    }

    private States _currentState;

    private final IVisualEngine _engine;

    public PuffshroomVisualObject(PuffShroomGameObject gameObject, IVisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Puffshroom/Puffshroom_0.png")));

        gameObject.subscribeToShootingEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    System.out.println("Shooting: " + gameObject.getCoordinate().x() + "," + gameObject.getCoordinate().y());
//                    var bulletVisualObject = new ShroomBulletVisualObject((ShroomBulletGameObject) gameObject, engine);
//                    _engine.spawnVisualObject(bulletVisualObject);
                });
            }
        });
//        gameObject.subscribeToSleepingEvent(new IEventSubscriber() {//notify//fix this later
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                Platform.runLater(() -> {
//                    _currentState = ScaredyshroomVisualObject.States.SLEEPING;
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
        super.playAnimation(PuffshroomAnimations.getFrames((PuffshroomAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
            if (_engine instanceof VisualNightEngine) {
            this._currentState = States.STANDING;
        } else {
            this._currentState = States.SLEEPING;
        }
        if (_currentState.equals(PuffshroomVisualObject.States.STANDING)) {
//            playAnimation(PuffshroomAnimations.Animations.STANDING, Duration.millis(80));//standing
        } else if (_currentState.equals(PuffshroomVisualObject.States.SLEEPING)) {
            playAnimation(PuffshroomAnimations.Animations.SLEEPING, Duration.millis(20));//standing
        }
    }

    public PuffshroomVisualObject changeStateTo(PuffshroomVisualObject.States state) {
        switch (state) {
            case SLEEPING -> {
                _currentState = States.SLEEPING;
//                playAnimation(ScaredyShroomAnimations.Animations.SLEEPING, Duration.millis(80));
            }
            case STANDING -> {
                _currentState = States.STANDING;
            }
        }
        return null;
    }
}

