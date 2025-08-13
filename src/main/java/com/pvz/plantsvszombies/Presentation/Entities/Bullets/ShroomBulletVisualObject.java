package com.pvz.plantsvszombies.Presentation.Entities.Bullets;

import com.pvz.plantsvszombies.Domain.Entities.Bullets.ShroomBulletGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractAnimatedVisualObject;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ShroomBulletVisualObject  extends AbstractAnimatedVisualObject {
        public enum States {
            MOVING,
            COLLIDED
        }

        private final VisualEngine _engine;
        private ShroomBulletVisualObject.States _state;

    public ShroomBulletVisualObject(ShroomBulletGameObject gameObject, VisualEngine engine) {
        super._gameObject = gameObject;
        this._engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Bullets/ShroomBullet/Moving/ShroomBullet_0.png")));
        _node.setManaged(false);

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - 0.5 * height);

//        ((ShroomBulletGameObject) _gameObject).subscribeToMovementEvent(new IEventSubscriber() {
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                Platform.runLater(() -> {
//                    _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - 0.5 * height);
//                });
////                System.out.println(gameObject.getCoordinate().x());
//
////                _node.setLayoutY(gameObject.getCoordinate().y());
////                _node.setTranslateX(_node.getTranslateX() + gameObject.getCoordinate().x() - old_x);
//            }
//        });
//
//        ((ShroomBulletGameObject) _gameObject).subscribeToCollisionEvent(new IEventSubscriber() {
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                changeStateTo(ShroomBulletVisualObject.States.COLLIDED);
//            }
//        });
//
//        var temp_this = this;
//        ((ShroomBulletGameObject) _gameObject).subscribeToDisposeEvent(new IEventSubscriber() {
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                _engine.disposeObject(temp_this); // Needs to be edited
//            }
//        });
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(ShroomBulletAnimations.getFrames((ShroomBulletAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        changeStateTo(ShroomBulletVisualObject.States.MOVING);
    }

        public ShroomBulletVisualObject changeStateTo(ShroomBulletVisualObject.States state) {
        switch (state) {
            case MOVING -> {
                _state = ShroomBulletVisualObject.States.MOVING;
                playAnimation(ShroomBulletAnimations.Animations.MOVING, Duration.millis(20));//standing
//                GeneralTransformAnimation.attach(this).transformX(10, VisualEngine.getInstance().getWidth() / 2.0);
            }
            case COLLIDED -> {
                _state = ShroomBulletVisualObject.States.COLLIDED;
                Platform.runLater(() -> {
                    ((ImageView) _node).setImage(new Image(GlobalSettings.getResource("graphics/Bullets/PeaShroomExplode/PeaShroomExplode_0.png")));
                    GeneralFadingAnimation.attach(this).fadeOut(Duration.millis(300)).setOnFinished((e) -> {
                        _engine.disposeObject(this);
                    });
                });
            }
        }
        return null;
    }

}
