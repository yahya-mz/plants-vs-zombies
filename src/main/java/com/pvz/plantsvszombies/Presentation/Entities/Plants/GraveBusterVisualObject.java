package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.GraveGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.GraveBusterGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundManager;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundType;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GraveBusterAnimations;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.WallNutAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import com.pvz.plantsvszombies.Domain.Entities.MapBlock;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class GraveBusterVisualObject extends AbstractPlantVisualObject{
    private final VisualEngine _engine;

    public GraveBusterVisualObject(GraveBusterGameObject gameObject, VisualEngine engine) {
        super._gameObject = gameObject;
        this._engine = engine;

        var temp_this = this;
        gameObject.subscribeToEatEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    setOnAnimationFinished(e -> {
                        SoundManager.stop(SoundType.GRAVE_BUSTER);
                        stopAnimation();
                        _engine.disposeObject(temp_this);
                    });
                });
            }
        });

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/GraveBuster/GraveBuster_0.png")));
        ((ImageView) _node).setFitWidth(85);
        ((ImageView) _node).setFitHeight(85);
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(GraveBusterAnimations.getFrames((GraveBusterAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(GraveBusterAnimations.getFrames((GraveBusterAnimations.Animations) animation), frameDuration, cycleCount);
    }

    @Override
    public void spawn() {
        Platform.runLater(() -> {
            _gameObject.spawn();
            // اجرای انیمیشن "شروع خوردن" بلافاصله بعد از اسپاون
            var framesCount = GraveBusterAnimations.getFrames(GraveBusterAnimations.Animations.EATING).length;
            playAnimation(GraveBusterAnimations.Animations.EATING,
                    Duration.millis(GraveBusterGameObject.EAT_TIME.toMillis()).divide(framesCount), 1);
        });
        SoundManager.play(SoundType.GRAVE_BUSTER);
    }

}
