package com.pvz.plantsvszombies.Presentation.Entities;


import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.WallNutGameObject;
import com.pvz.plantsvszombies.Presentation.Animations.WallnutAnimations;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class TallnutVisualObject extends AbstractAnimatedVisualObject {


    public enum States {
        FULLHEALTH,
        CRACKED1,
        CRACKED2
    }

    private WallNutGameObject _gameObject;

    private States _currentState;

    private GeneralFadingAnimation _fadingAnimation;
    private GeneralTransformAnimation _transformAnimation;

    private VisualEngine _engine;

    public TallnutVisualObject(WallNutGameObject gameObject, VisualEngine engine) {
        _engine = engine;
//
//        gameObject.subscribeToFullHealthEvent(new IEventSubscriber() {
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                System.out.println("Tallnut will be full health again");
//                changeStateTo(States.FULLHEALTH);
//            }
//        });
//        gameObject.subscribeToBeingEatenEvent1(new IEventSubscriber() {
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                System.out.println("Tallnut being cracked1");
//                changeStateTo(States.CRACKED1);
//            }
//        });
//        gameObject.subscribeToFullHealthEvent2(new IEventSubscriber() {
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                System.out.println("Tallnut being cracked2");
//                changeStateTo(States.FULLHEALTH);
//            }
//        });

        _visualCoordinate = gameObject.getCoordinate();
        _gameObject = gameObject;
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Plants/Tallnut/Tallnut_0.png")));

        _node.setCursor(Cursor.HAND);
        _node.setTranslateY(_visualCoordinate.y());
        _node.setTranslateX(_visualCoordinate.x());


    }

    @Override
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, WallnutAnimations.getFrames((WallnutAnimations.Animations) animation), Duration.millis(80));
    }

    @Override
    public void spawn() {
        changeStateTo(States.FULLHEALTH);
        playAnimation(WallnutAnimations.Animations.STANDING);
    }

    public SkySunVisualObject changeStateTo(States state) {
        switch (state) {
            case FULLHEALTH -> {
                _currentState = States.FULLHEALTH;
                playAnimation(WallnutAnimations.Animations.STANDING);

            }
            case CRACKED1 -> {
                if (_currentState == States.CRACKED1) {
                    break;
                }
                if (_currentState.equals(States.FULLHEALTH)) {
                    changeStateTo(States.CRACKED1);
                    playAnimation(WallnutAnimations.Animations.CRACKED1);
                }
            }

            case CRACKED2 -> {
                _currentState = States.CRACKED2;
                playAnimation(WallnutAnimations.Animations.CRACKED2);

            }
        }
        return null;
    }
}
