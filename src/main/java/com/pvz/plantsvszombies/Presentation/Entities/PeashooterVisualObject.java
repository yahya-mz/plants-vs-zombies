package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeaShooterGameObject;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.GUI.MainApp;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class PeashooterVisualObject extends AbstractAnimatedVisualObject {
    public enum States {
        Shooting,
        Standing
    }

    private Coordinate _visualCoordinate;
    private PeaShooterGameObject _gameObject;

    private States _currentState;

    public PeashooterVisualObject(PeaShooterGameObject gameObject) {
        _gameObject = gameObject;
        gameObject.subscribeToShootingEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(IGameObject gameObject) {
                System.out.println("Fading out");
                changeStateTo(States.Shooting);
            }
        });
        var temp_this = this;
        gameObject.subscribeToEatenEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(IGameObject gameObject) {
                VisualEngine.getInstance().disposeObject(temp_this);
            }
        });
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Peashooter/Peashooter_0.png")));
        _node.setTranslateY(_visualCoordinate.y());
        _node.setTranslateX(_visualCoordinate.x());


    }

    @Override
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, PeaShooterAnimation.getFrames((PeaShooterAnimation.Animations) animation));
    }

    @Override
    public void spawn() {
        //changeStateTo(States.Standing);
        playAnimation(PeaShooterAnimation.Animations.STANDING);//standing
    }

    public PeashooterVisualObject changeStateTo(States state) {
        switch (state) {
            case Shooting -> {
                _currentState = States.Shooting;
                playAnimation(PeaShooterAnimation.Animations.SHOOTING); // فریم‌های پرتاب توپ
                changeStateTo(States.Standing);
            }
            case Standing -> {
                _currentState = States.Standing;
                playAnimation(PeaShooterAnimation.Animations.STANDING); // فریم‌های ایستادن
            }
        }
        return null;
    }
}
