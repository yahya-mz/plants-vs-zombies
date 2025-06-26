package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.Plants.PeashooterGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralFadingAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralTransformAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.PeashooterAnimation;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class NormalBulletVisualObject extends AbstractVisualObject {

    private final NormalBulletGameObject _gameObject;
    private VisualEngine _engine;

    public enum States {
        MOVING,
        COLLIDE
    }

    public NormalBulletVisualObject(NormalBulletGameObject gameObject, VisualEngine engine) {
        this._gameObject = gameObject;
        this._engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Bullets/PeaNormal/PeaNormal_0.png")));
        _node.setManaged(false);

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - 0.5 * height);

        _gameObject.subscribeToMovementEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - 0.5 * height);
                });
//                System.out.println(gameObject.getCoordinate().x());

//                _node.setLayoutY(gameObject.getCoordinate().y());
//                _node.setTranslateX(_node.getTranslateX() + gameObject.getCoordinate().x() - old_x);
            }
        });

        _gameObject.subscribeToCollisionEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                changeStateTo(States.COLLIDE);
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
                Platform.runLater(() -> {
                    ((ImageView) _node).setImage(new Image(GlobalSettings.getResource("graphics/Bullets/PeaNormalExplode/PeaNormalExplode_0.png")));
                    GeneralFadingAnimation.attach(this).fadeOut(Duration.millis(300)).setOnFinished((e) -> {
                        _engine.disposeObject(this);
                    });
                });
            }
        }
        return null;
    }
}
