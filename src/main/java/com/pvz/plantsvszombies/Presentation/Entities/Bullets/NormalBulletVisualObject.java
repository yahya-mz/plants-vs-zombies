package com.pvz.plantsvszombies.Presentation.Entities.Bullets;

import com.pvz.plantsvszombies.Domain.Entities.Bullets.NormalBulletGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralFadingAnimation;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractVisualObject;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class NormalBulletVisualObject extends AbstractVisualObject {
    public enum States {
        MOVING,
        COLLIDED
    }

    private final VisualEngine _engine;
    private States _state;

    public NormalBulletVisualObject(NormalBulletGameObject gameObject, VisualEngine engine) {
        super._gameObject = gameObject;
        this._engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Bullets/PeaNormal/PeaNormal_0.png")));
        _node.setManaged(false);

        var height = (((ImageView) _node).getImage()).getHeight();
        var width = (((ImageView) _node).getImage()).getWidth();

        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - 0.5 * height);

        ((NormalBulletGameObject) _gameObject).subscribeToMovementEvent(bulletObj -> Platform.runLater(() -> {
            _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - 0.5 * height);
        }));

        ((NormalBulletGameObject) _gameObject).subscribeToCollisionEvent(gameObject1 -> changeStateTo(States.COLLIDED));

        var temp_this = this;
        _gameObject.subscribeToDisposeEvent(bulletObj -> {
                _engine.disposeObject(temp_this);
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
                _state = States.MOVING;
//                GeneralTransformAnimation.attach(this).transformX(10, VisualEngine.getInstance().getWidth() / 2.0);
            }
            case COLLIDED -> {
                _state = States.COLLIDED;
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
