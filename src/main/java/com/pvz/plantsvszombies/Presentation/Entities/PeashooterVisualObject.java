package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class PeashooterVisualObject extends AbstractPlantVisualObject {
    public enum States {
        Shooting,
        Standing
    }

    private PeashooterGameObject _gameObject;

    private States _currentState;

    private VisualEngine _engine;

    public PeashooterVisualObject(PeashooterGameObject gameObject, VisualEngine engine) {//وابستگی ها و مقدار دهی
        _gameObject = gameObject;
        _engine = engine;

        gameObject.subscribeToShootingEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    System.out.println("Shooting: " + gameObject.getCoordinate().x() + "," + gameObject.getCoordinate().y());
                    changeStateTo(States.Shooting);
                    var bulletVisualObject = new NormalBulletVisualObject((NormalBulletGameObject) gameObject);
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
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Peashooter/Peashooter_0.png")));
//        _node.setTranslateY(_visualCoordinate.y());
//        _node.setTranslateX(_visualCoordinate.x());

    }

    @Override
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, PeashooterAnimation.getFrames((PeashooterAnimation.Animations) animation), Duration.millis(90));
    }

    @Override
    public void spawn() {
        Platform.runLater(() -> {
            System.out.println("demodemo");
            playAnimation(PeashooterAnimation.Animations.STANDING);//standing

        });
    }

    public PeashooterVisualObject changeStateTo(States state) {
        switch (state) {
            case Shooting -> {
                _currentState = States.Shooting;
//                playAnimation(PeashooterAnimation.Animations.SHOOTING); // فریم‌های پرتاب توپ
                changeStateTo(States.Standing);
            }
            case Standing -> {
                _currentState = States.Standing;
//                playAnimation(PeashooterAnimation.Animations.STANDING); // فریم‌های ایستادن
            }
        }
        return null;
    }
}
