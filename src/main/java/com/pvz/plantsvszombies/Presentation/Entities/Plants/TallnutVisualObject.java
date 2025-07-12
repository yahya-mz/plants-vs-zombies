package com.pvz.plantsvszombies.Presentation.Entities.Plants;


import com.pvz.plantsvszombies.Domain.Entities.Plants.TallNutGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractAnimatedVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.SkySunVisualObject;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class TallnutVisualObject extends AbstractPlantVisualObject {
    public enum States {
        FULL_HEALTH,
        CRACKED1,
        CRACKED2
    }

    private States _currentState;

    private GeneralFadingAnimation _fadingAnimation;
    private GeneralTransformAnimation _transformAnimation;

    private final IVisualEngine _engine;

    public TallnutVisualObject(TallNutGameObject gameObject, IVisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;
//
//        gameObject.subscribeToFullHealthEvent(new IEventSubscriber() {
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                System.out.println("Tallnut will be full health again");
//                changeStateTo(States.FULL_HEALTH);
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
//                changeStateTo(States.FULL_HEALTH);
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
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(TallnutAnimations.getFrames((TallnutAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        changeStateTo(States.FULL_HEALTH);
        playAnimation(TallnutAnimations.Animations.FULL_HEALTH, Duration.millis(90));
    }

    public SkySunVisualObject changeStateTo(States state) {
        switch (state) {
            case FULL_HEALTH -> {
                _currentState = States.FULL_HEALTH;
                playAnimation(TallnutAnimations.Animations.FULL_HEALTH, Duration.millis(90));
            }
            case CRACKED1 -> {
                if (_currentState == States.CRACKED1) {
                    break;
                }
                if (_currentState.equals(States.FULL_HEALTH)) {
                    changeStateTo(States.CRACKED1);
                    playAnimation(TallnutAnimations.Animations.CRACKED1, Duration.millis(90));
                }
            }

            case CRACKED2 -> {
                _currentState = States.CRACKED2;
                playAnimation(TallnutAnimations.Animations.CRACKED2, Duration.millis(90));
            }
        }
        return null;
    }
}
