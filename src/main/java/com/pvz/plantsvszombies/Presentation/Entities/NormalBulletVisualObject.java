package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralTransformAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.PeashooterAnimation;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NormalBulletVisualObject extends AbstractVisualObject {
    private final NormalBulletGameObject _gameObject;

    public enum States {
        MOVING,
        COLLIDE
    }

    public NormalBulletVisualObject(NormalBulletGameObject gameObject) {
        _gameObject = gameObject;
        gameObject.subscribeToCollisionEvent((object) -> {
            changeStateTo(States.COLLIDE);
        });
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Bullets/PeaNormal/PeaNormal_0.png")));
        _node.setTranslateY(_visualCoordinate.y());
        _node.setTranslateX(_visualCoordinate.x());

        _gameObject.subscribeToMovementEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
//                i++;
//                System.out.println(gameObject.getCoordinate().x());
                Bounds boundsInScene = _node.localToScene(_node.getBoundsInLocal());
                double old_x = boundsInScene.getCenterX() - 640;
                _node.setTranslateX(_node.getTranslateX() + gameObject.getCoordinate().x() - old_x);
            }
        });
    }

    int i = 0;

    @Override
    public void spawn() {
        changeStateTo(States.MOVING);
    }

    public NormalBulletVisualObject changeStateTo(States state) {
        switch (state) {
            case MOVING -> {
//                GeneralTransformAnimation.attach(this).transformX(10, VisualEngine.getInstance().getWidth() / 2.0);
            }
            case COLLIDE -> {

            }
        }
        return null;
    }
}
