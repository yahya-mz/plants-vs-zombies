package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralFadingAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralTransformAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.SunAnimations;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractAnimatedVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.SkySunVisualObject;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
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

    private SunGameObject _gameObject;

    private States _currentState;

    private GeneralFadingAnimation _fadingAnimation;
    private GeneralTransformAnimation _transformAnimation;

    private VisualEngine _engine;

    public FlowerSunVisualObject(SunGameObject gameObject, VisualEngine engine) {
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
        super.playAnimation(animation, SunAnimations.getFrames((SunAnimations.Animations) animation), Duration.millis(80));
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
                //my idea--change this
                _transformAnimation = GeneralTransformAnimation.attach(this).transform(4, 4, 20, 20)
                        .then((event) -> {
                            _transformAnimation = GeneralTransformAnimation.attach(this).transform(4, 4, 10, -20);
                        });

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
                if (_currentState.equals(States.DROPPING)) {//we may not need this
                    _transformAnimation.interrupt();
                }
                _currentState = States.COLLECTING;
                System.out.println(this.getNode().getTranslateY());
                //change this
                _transformAnimation.transform(-6, -3, 338 + this.getNode().getTranslateY(), 300 + this.getNode().getTranslateY());
                changeStateTo(States.FADING_OUT);

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