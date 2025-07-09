package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralFadingAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralTransformAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.SunAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualDayEngine;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

public class SkySunVisualObject extends AbstractAnimatedVisualObject {


    public enum States {
        DROPPING,
        COLLECTING,
        FADING_OUT
    }

    private SunGameObject _gameObject;

    private States _currentState;

    private GeneralFadingAnimation _fadingAnimation;
    private GeneralTransformAnimation _transformAnimation;

    private VisualDayEngine _engine;

    public SkySunVisualObject(SunGameObject gameObject, VisualDayEngine engine) {
        _engine = engine;
        gameObject.subscribeToTimeOut(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                System.out.println("Fading out");
                changeStateTo(States.FADING_OUT);
            }
        });
        _visualCoordinate = gameObject.getCoordinate();
        _gameObject = gameObject;
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Sun/Sun_0.png")));
        ((ImageView) _node).setFitWidth(80);
        ((ImageView) _node).setFitHeight(80);
        ((ImageView) _node).setPreserveRatio(true);

        _node.setCursor(Cursor.HAND);
        _node.setManaged(false);
        _node.relocate(_visualCoordinate.x(),_visualCoordinate.y());
        _node.setTranslateY(-50);
//        _node.setTranslateX(_visualCoordinate.x());

        _node.setOnMouseClicked((e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                changeStateTo(States.COLLECTING);
            }
        });
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(SunAnimations.getFrames((SunAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        _gameObject.spawn();
        changeStateTo(States.DROPPING);
        playAnimation(SunAnimations.Animations.SHINING,Duration.millis(80));
    }

    public SkySunVisualObject changeStateTo(States state) {
        switch (state) {
            case DROPPING -> {
                _currentState = States.DROPPING;
                _transformAnimation = GeneralTransformAnimation.attach(this).transformY(10, GlobalSettings.HEIGHT / 2.0);
                // +10 means going down
            }
            case COLLECTING -> {
                if (_currentState == States.COLLECTING) {
                    return this;
                }
                _gameObject.gain();
                if (_currentState.equals(States.FADING_OUT)) {
                    _fadingAnimation.interrupt();
                    _fadingAnimation = null;
                    _node.setOpacity(1);
                }
                if (_currentState.equals(States.DROPPING)) {
                    _transformAnimation.interrupt();
                }
                _currentState = States.COLLECTING;
                var demo = this._node.getTranslateX();
                var demo2 = this._node.getTranslateY();

                var verticalDistance = 9 - this._node.getTranslateY();
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
