package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.IceShroomGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.IceshroomAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import com.pvz.plantsvszombies.Presentation.Engines.VisualNightEngine;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class IceshroomVisualObject extends AbstractPlantVisualObject{
    public enum States {
        STANDING,
        SLEEPING,
        EXPLODED
    }

    private IceshroomVisualObject.States _currentState;

    private final VisualEngine _engine;

    public IceshroomVisualObject(IceShroomGameObject gameObject, VisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        this._engine = engine;

        var temp_this = this;
        gameObject.subscribeToExplosionEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    changeStateTo(States.EXPLODED);
                    setOnAnimationFinished(e -> {
                        _engine.disposeObject(temp_this);
                    });
                });
            }
        });

//        var temp_this = this;
//        gameObject.subscribeToEatenEvent(new IEventSubscriber() {//after spawning it will be eaten
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                stopAnimation();
//                _engine.disposeObject(temp_this);
//            }
//        });

        gameObject.subscribeToWakeUpEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    changeStateTo(States.STANDING);
                });
            }
        });

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Iceshroom/Iceshroom_0.png")));
        ((ImageView) _node).setFitWidth(85);
        ((ImageView) _node).setFitHeight(85);
    }

        @Override
        public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(IceshroomAnimations.getFrames((IceshroomAnimations.Animations) animation), frameDuration);
    }

        public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(IceshroomAnimations.getFrames((IceshroomAnimations.Animations) animation), frameDuration, cycleCount);
    }

        @Override
        public void spawn() {
            if (_engine instanceof VisualNightEngine) {
                this._currentState = IceshroomVisualObject.States.STANDING;
            } else {
                this._currentState = IceshroomVisualObject.States.SLEEPING;
            }
            if (_currentState.equals(IceshroomVisualObject.States.STANDING)) {
                playAnimation(IceshroomAnimations.Animations.STANDING, Duration.millis(80));//standing
            }
        }

    public IceshroomVisualObject changeStateTo(IceshroomVisualObject.States state) {
        switch (state) {
            case SLEEPING -> {
                _currentState = IceshroomVisualObject.States.SLEEPING;
                if (_currentState.equals(IceshroomVisualObject.States.SLEEPING)) {
                    playAnimation(IceshroomAnimations.Animations.SLEEPING, Duration.millis(20));//standing
                }
            }
            case STANDING -> {
                _currentState = IceshroomVisualObject.States.STANDING;
            }
            case EXPLODED -> {
                _currentState = States.EXPLODED;
                playAnimation(IceshroomAnimations.Animations.EXPLODED, Duration.millis(80) , 1);
            }
        }
        return null;
    }

//        public void spawn() {
//        Platform.runLater(() -> {
//            _gameObject.spawn();
//            var framesCount = IceshroomAnimations.getFrames(IceshroomAnimations.Animations.STANDING).length;
//            playAnimation(IceshroomAnimations.Animations.STANDING, Duration.millis(IceShroomGameObject.EXPLOSION_TIME.toMillis()).divide(framesCount), 1);//standing//the animation is not inf is one time only
//        });
//    }
    
}
