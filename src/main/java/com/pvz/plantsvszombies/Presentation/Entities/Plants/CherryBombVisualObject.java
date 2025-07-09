package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.Plants.CherryBombGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class CherryBombVisualObject extends AbstractPlantVisualObject {

    private final IVisualEngine _engine;

    public CherryBombVisualObject(CherryBombGameObject gameObject, IVisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        this._engine = engine;

        var temp_this = this;
        gameObject.subscribeToExplosionEvent(new IEventSubscriber() {//notify//we dont need this
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    playAnimation(CherryBombAnimations.Animations.EXPLODED, Duration.millis(50), 1);
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

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/CherryBomb/CherryBomb_0.png")));
        ((ImageView) _node).setFitWidth(85);
        ((ImageView) _node).setFitHeight(85);
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(CherryBombAnimations.getFrames((CherryBombAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(CherryBombAnimations.getFrames((CherryBombAnimations.Animations) animation), frameDuration, cycleCount);
    }

    @Override
    public void spawn() {
        Platform.runLater(() -> {
            _gameObject.spawn();
            var framesCount = CherryBombAnimations.getFrames(CherryBombAnimations.Animations.EXPLODING).length;
            playAnimation(CherryBombAnimations.Animations.EXPLODING, Duration.millis(CherryBombGameObject.EXPLOSION_TIME.toMillis()).divide(framesCount), 1);//standing//the animation is not inf is one time only
        });
    }

}
