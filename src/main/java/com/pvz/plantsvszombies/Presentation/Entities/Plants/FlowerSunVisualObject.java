package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralFadingAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralTransformAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.SunAnimations;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractAnimatedVisualObject;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

public class FlowerSunVisualObject extends AbstractAnimatedVisualObject {
    public enum States {
        DROPPING,
        COLLECTING,
        FADING_OUT
    }

    private final double _width;
    private final double _height;

    private States _currentState;

    private GeneralFadingAnimation _fadingAnimation;
    private GeneralTransformAnimation _transformAnimation;

    private final IVisualEngine _engine;

    public FlowerSunVisualObject(SunGameObject gameObject, IVisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Sun/Sun_0.png")));
        ((ImageView) _node).setFitWidth(50);
        ((ImageView) _node).setFitHeight(50);
        ((ImageView) _node).setPreserveRatio(true);

        _node.setCursor(Cursor.HAND);
//        _node.setTranslateY(_visualCoordinate.y());
//        _node.setTranslateX(_visualCoordinate.x());
        _node.setManaged(false);

        _height = ((Image) ((ImageView) _node).getImage()).getHeight();
        _width = ((Image) ((ImageView) _node).getImage()).getWidth();
        _node.relocate(_visualCoordinate.x() - 0.5 * _width, _visualCoordinate.y() - 0.5 * _height);

        _node.setOnMouseClicked((e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                changeStateTo(States.COLLECTING);
            }
        });
        gameObject.subscribeToTimeOut(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                System.out.println("Fading out");
                changeStateTo(States.FADING_OUT);
            }
        });
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(SunAnimations.getFrames((SunAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        changeStateTo(States.DROPPING);
        playAnimation(SunAnimations.Animations.SHINING, Duration.millis(80));

    }

    public FlowerSunVisualObject changeStateTo(States state) {
        switch (state) {
            case DROPPING -> {
                _currentState = States.DROPPING;
                Platform.runLater(() -> {
                    _transformAnimation = GeneralTransformAnimation.attach(this).transform(5.5, 11, 20, -50)
                            .then((event) -> {
                                _transformAnimation = GeneralTransformAnimation.attach(this).transform(1.65, 2, 20, 95);
                            });
                });

            }
            // +10 means going down

            case COLLECTING -> {
                if (_currentState == FlowerSunVisualObject.States.COLLECTING) {
                    return this;
                }
                ((SunGameObject) _gameObject).gain();
                if (_currentState.equals(FlowerSunVisualObject.States.FADING_OUT)) {
                    _fadingAnimation.interrupt();
                    _fadingAnimation = null;
                    _node.setOpacity(1);
                }
                if (_currentState.equals(FlowerSunVisualObject.States.DROPPING)) {
                    _transformAnimation.interrupt();
                }
                _currentState = FlowerSunVisualObject.States.COLLECTING;
                var demo = this._node.getTranslateX();
                var demo2 = this._node.getTranslateY();

                var verticalDistance = 9 - this._visualCoordinate.y() + 0.5 * _height;
                var horizontalDistance = 166 - _visualCoordinate.x();
                _transformAnimation.transform(Math.max(6 * horizontalDistance / verticalDistance, 6), Math.max(6 * verticalDistance / horizontalDistance, 6), horizontalDistance, verticalDistance);
                _fadingAnimation = GeneralFadingAnimation.attach(this).fadeOut(Duration.millis(4000))
                        .setOnFinished((e) -> {
                            _engine.disposeObject(this);
                            _gameObject.dispose();
                        });

            }

            case FADING_OUT -> {
                _currentState = States.FADING_OUT;
                _fadingAnimation = GeneralFadingAnimation.attach(this).fadeOut(Duration.millis(2500))
                        .setOnFinished((e) -> {
//                            ((Pane) this.getNode().getParent()).getChildren().remove(_node);
                            _engine.disposeObject(this);
                            _gameObject.dispose();
                        });
            }
        }
        return null;
    }
}