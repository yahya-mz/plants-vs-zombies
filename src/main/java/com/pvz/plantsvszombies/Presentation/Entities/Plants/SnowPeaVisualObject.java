package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.Bullets.SnowBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.SnowPeaGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import com.pvz.plantsvszombies.Presentation.Entities.Bullets.SnowBulletVisualObject;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class SnowPeaVisualObject extends AbstractPlantVisualObject {

    private final VisualEngine _engine;

    public SnowPeaVisualObject(SnowPeaGameObject gameObject, VisualEngine engine) {//وابستگی ها و مقدار دهی
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/SnowPea/SnowPea_0.png")));

        gameObject.subscribeToShootingEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    System.out.println("Shooting: " + gameObject.getCoordinate().x() + "," + gameObject.getCoordinate().y());
                    var bulletVisualObject = new SnowBulletVisualObject((SnowBulletGameObject) gameObject, engine);
                    _engine.spawnVisualObject(bulletVisualObject);
                });
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
        super.playAnimation(SnowPeaAnimations.getFrames((SnowPeaAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        playAnimation(SnowPeaAnimations.Animations.STANDING, Duration.millis(20));//standing
    }
}
