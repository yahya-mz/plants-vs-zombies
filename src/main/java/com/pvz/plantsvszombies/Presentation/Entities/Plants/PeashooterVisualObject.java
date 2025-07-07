package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Entities.NormalBulletVisualObject;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class PeashooterVisualObject extends AbstractPlantVisualObject {
    private PeashooterGameObject _gameObject;
    private VisualEngine _engine;

    public PeashooterVisualObject(PeashooterGameObject gameObject, VisualEngine engine) {//وابستگی ها و مقدار دهی
        _gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Peashooter/Peashooter_0.png")));

        gameObject.subscribeToShootingEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    System.out.println("Shooting: " + gameObject.getCoordinate().x() + "," + gameObject.getCoordinate().y());
                    var bulletVisualObject = new NormalBulletVisualObject((NormalBulletGameObject) gameObject,engine);
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
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, PeashooterAnimation.getFrames((PeashooterAnimation.Animations) animation), Duration.millis(90));
    }

    @Override
    public void spawn() {
        playAnimation(PeashooterAnimation.Animations.STANDING);//standing
    }
}
