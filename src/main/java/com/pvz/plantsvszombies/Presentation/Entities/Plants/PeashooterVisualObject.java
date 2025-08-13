package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class PeashooterVisualObject extends AbstractPlantVisualObject {
    private final VisualEngine _engine;

    public PeashooterVisualObject(PeashooterGameObject gameObject, VisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Peashooter/Peashooter_0.png")));

        gameObject.subscribeToShootingEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {

            }
        });
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
        super.playAnimation(PeashooterAnimation.getFrames((PeashooterAnimation.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        playAnimation(PeashooterAnimation.Animations.STANDING, Duration.millis(20));//standing
    }
}
