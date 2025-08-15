package com.pvz.plantsvszombies.Presentation.Entities.Plants;


import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.BloverGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.CherryBombGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PlanternGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundManager;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundType;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.BloverAnimations;
import com.pvz.plantsvszombies.Presentation.Animations.CherryBombAnimations;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.PlanternAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class PlanternVisualObject extends AbstractPlantVisualObject {
    public enum States {
        STANDING
    }

    private PlanternVisualObject.States _currentState = PlanternVisualObject.States.STANDING;


    private final IVisualEngine _engine;

    public PlanternVisualObject(PlanternGameObject gameObject, IVisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Plantern/Plantern_0.png")));
        ((ImageView) _node).setFitWidth(85);
        ((ImageView) _node).setFitHeight(85);

        var temp_this = this;
        gameObject.subscribeLightUpEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    playAnimation(PlanternAnimations.Animations.STANDING, Duration.millis(50), 10);
                    setOnAnimationFinished(e -> {
                        _engine.disposeObject(temp_this);
                    });
                });
            }
        });
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(PlanternAnimations.getFrames((PlanternAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(PlanternAnimations.getFrames((PlanternAnimations.Animations) animation), frameDuration, cycleCount);

    }

    @Override
    public void spawn() {
        Platform.runLater(() -> {
            _gameObject.spawn();
//            SoundManager.play(SoundType.CHERRY_BOMB_EXPLOSION);
        });
    }
}
