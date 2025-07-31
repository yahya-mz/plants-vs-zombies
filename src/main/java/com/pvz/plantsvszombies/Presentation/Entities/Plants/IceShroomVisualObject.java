package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.Plants.IceShroomGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IceShroomAnimations;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class IceShroomVisualObject extends AbstractPlantVisualObject{
        private final IVisualEngine _engine;

    public IceShroomVisualObject(IceShroomGameObject gameObject, IVisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        this._engine = engine;

        var temp_this = this;
//        gameObject.subscribeToExplosionEvent(new IEventSubscriber() {//notify
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                Platform.runLater(() -> {
//                    playAnimation(IceShroomAnimations.Animations.EXPLODED, Duration.millis(50), 1);
//                    setOnAnimationFinished(e -> {
//                        _engine.disposeObject(temp_this);
//                    });
//                });
//            }
//        });

//        var temp_this = this;
//        gameObject.subscribeToEatenEvent(new IEventSubscriber() {//after spawning it will be eaten
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                stopAnimation();
//                _engine.disposeObject(temp_this);
//            }
//        });

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/IceShroom/IceShroom_0.png")));
        ((ImageView) _node).setFitWidth(85);
        ((ImageView) _node).setFitHeight(85);
    }

        @Override
        public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(IceShroomAnimations.getFrames((IceShroomAnimations.Animations) animation), frameDuration);
    }

        public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(IceShroomAnimations.getFrames((IceShroomAnimations.Animations) animation), frameDuration, cycleCount);
    }

        @Override
        public void spawn() {
        Platform.runLater(() -> {
            _gameObject.spawn();
            var framesCount = IceShroomAnimations.getFrames(IceShroomAnimations.Animations.BLOWING).length;
            playAnimation(IceShroomAnimations.Animations.BLOWING, Duration.millis(IceShroomGameObject.EXPLOSION_TIME.toMillis()).divide(framesCount), 1);//standing//the animation is not inf is one time only
        });
    }
    
}
