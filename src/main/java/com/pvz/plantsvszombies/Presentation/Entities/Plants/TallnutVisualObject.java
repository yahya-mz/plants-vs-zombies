package com.pvz.plantsvszombies.Presentation.Entities.Plants;


import com.pvz.plantsvszombies.Domain.Entities.Plants.TallNutGameObject;
import com.pvz.plantsvszombies.Presentation.Animations.WallNutAnimations;
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
        FULLHEALTH,
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
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(WallNutAnimations.getFrames((WallNutAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        changeStateTo(States.FULLHEALTH);
        playAnimation(WallNutAnimations.Animations.FULL_HEALTH, Duration.millis(90));
    }

    public SkySunVisualObject changeStateTo(States state) {
        switch (state) {
            case FULLHEALTH -> {
                _currentState = States.FULLHEALTH;
                playAnimation(WallNutAnimations.Animations.FULL_HEALTH, Duration.millis(90));
            }
            case CRACKED1 -> {
                if (_currentState == States.CRACKED1) {
                    break;
                }
                if (_currentState.equals(States.FULLHEALTH)) {
                    changeStateTo(States.CRACKED1);
                    playAnimation(WallNutAnimations.Animations.CRACKED1, Duration.millis(90));
                }
            }

            case CRACKED2 -> {
                _currentState = States.CRACKED2;
                playAnimation(WallNutAnimations.Animations.CRACKED2, Duration.millis(90));
            }
        }
        return null;
    }
}
