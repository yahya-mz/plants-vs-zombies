package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.IGameObject;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.GUI.MainApp;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralFadingAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralTransformAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.SunAnimations;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.animation.Animation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class SkySunVisualObject extends AbstractAnimatedVisualObject {


    public enum States {
        DROPPING,
        COLLECTING,
        FADING_OUT
    }

    private Coordinate _visualCoordinate;
    private SunGameObject _gameObject;

    private States _currentState;

    private GeneralFadingAnimation _fadingAnimation;
    private GeneralTransformAnimation _transformAnimation;

    public SkySunVisualObject(SunGameObject gameObject) {
        gameObject.subscribeToTimeOut(new IEventSubscriber() {
            @Override
            public void _notify(IGameObject gameObject) {
                System.out.println("Fading out");
                changeStateTo(States.FADING_OUT);
            }
        });
        _visualCoordinate = gameObject.getCoordinate();
        _gameObject = gameObject;
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Sun/Sun_0.png")));

        _node.setCursor(Cursor.HAND);
        _node.setTranslateY(_visualCoordinate.y());
        _node.setTranslateX(_visualCoordinate.x());

        _node.setOnMouseClicked((e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                changeStateTo(States.COLLECTING);
            }
        });
    }

    @Override
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, SunAnimations.getFrames((SunAnimations.Animations) animation));
    }

    @Override
    public void spawn() {
        changeStateTo(States.DROPPING);
        playAnimation(SunAnimations.Animations.SHINING);
    }

    public SkySunVisualObject changeStateTo(States state) {
        switch (state) {
            case DROPPING -> {
                _currentState = States.DROPPING;
                _transformAnimation = GeneralTransformAnimation.attach(this).transformY(10, VisualEngine.getInstance().getLevelStage().getHeight() / 2);
                // +10 means going down
            }
            case COLLECTING -> {
                if (_currentState == States.COLLECTING) {
                    break;
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
                System.out.println(this.getNode().getTranslateY());
                _transformAnimation.transform(-10, -10, 640 + this.getNode().getTranslateY(), 364 + this.getNode().getTranslateY());

            }
            case FADING_OUT -> {
                _currentState = States.FADING_OUT;
                _fadingAnimation = GeneralFadingAnimation.attach(this).fadeOut(Duration.millis(2500))
                        .setOnFinished((e) -> {
//                            ((Pane) this.getNode().getParent()).getChildren().remove(_node);
                            VisualEngine.getInstance().disposeObject(this);
                            _gameObject.dispose();
                        });
            }
        }
        return null;
    }
}
