package com.pvz.plantsvszombies.Presentation.Entities.Bullets;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.SnowBulletGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralFadingAnimation;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractVisualObject;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class SnowBulletVisualObject extends AbstractVisualObject {
    public enum States {
        MOVING,
        COLLIDED
    }

    private final IVisualEngine _engine;
    private States _state;

    public SnowBulletVisualObject(SnowBulletGameObject gameObject, IVisualEngine engine) {
        super._gameObject = gameObject;
        this._engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Bullets/PeaIce/PeaIce_0.png")));
        _node.setManaged(false);

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - 0.5 * height);

        ((SnowBulletGameObject) _gameObject).subscribeToMovementEvent(new IEventSubscriber() {
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

        ((SnowBulletGameObject) _gameObject).subscribeToCollisionEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                changeStateTo(States.COLLIDED);
            }
        });

        var temp_this = this;
        ((SnowBulletGameObject) _gameObject).subscribeToDisposeEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                _engine.disposeObject(temp_this); // Needs to be edited
            }
        });
    }

    int i = 0;

    @Override
    public void spawn() {
        changeStateTo(States.MOVING);
    }

    public SnowBulletVisualObject changeStateTo(States state) {
        switch (state) {
            case MOVING -> {
                _state = States.MOVING;
//                GeneralTransformAnimation.attach(this).transformX(10, VisualEngine.getInstance().getWidth() / 2.0);
            }
            case COLLIDED -> {
                _state = States.COLLIDED;
                Platform.runLater(() -> {
//                    ((ImageView) _node).setImage(new Image(GlobalSettings.getResource("graphics/Bullets/PeaIceExplode/PeaIceExplode_0.png")));
                    GeneralFadingAnimation.attach(this).fadeOut(Duration.millis(300)).setOnFinished((e) -> {
                        _engine.disposeObject(this);
                    });
                });
            }
        }
        return null;
    }
}
