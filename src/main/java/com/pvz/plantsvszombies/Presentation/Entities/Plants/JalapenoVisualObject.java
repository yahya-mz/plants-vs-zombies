package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.JalapenoGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class JalapenoVisualObject extends AbstractPlantVisualObject {

    private final VisualEngine _engine;

    public JalapenoVisualObject(JalapenoGameObject gameObject, VisualEngine engine) {//وابستگی ها و مقدار دهی  change object
        super._gameObject = gameObject;
        _engine = engine;

        var temp_this = this;
        gameObject.subscribeToExplosionEvent(new IEventSubscriber() {//notify//we dont need this//بهتر نیست انیمیشن رو در اینجا تعریف کنیم؟؟؟
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    ((ImageView) _node).setImage(new Image(GlobalSettings.getResource("graphics/Plants/Jalapeno/FIRING/0.png")));
                    playAnimation(JalapenoAnimations.Animations.FIRING, Duration.millis(50), 1);
                    _node.setManaged(false);
                    ((ImageView) _node).setPreserveRatio(false);

                    _node.setTranslateX(-_engine.getWidth());
                    ((ImageView) _node).setFitWidth(2 * _engine.getWidth());
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
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Jalapeno/Jalapeno_0.png")));
        ((ImageView) _node).setFitWidth(85);
        ((ImageView) _node).setFitHeight(85);

    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(JalapenoAnimations.getFrames((JalapenoAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(JalapenoAnimations.getFrames((JalapenoAnimations.Animations) animation), frameDuration, cycleCount);

    }

    @Override
    public void spawn() {
        Platform.runLater(() -> {
            var framesCount = JalapenoAnimations.getFrames(JalapenoAnimations.Animations.EXPLODING).length;
            playAnimation(JalapenoAnimations.Animations.EXPLODING, Duration.millis(JalapenoGameObject.EXPLOSION_TIME.toMillis()).divide(framesCount), 1);//standing//the animation is not inf is one time only
        });
    }
}
