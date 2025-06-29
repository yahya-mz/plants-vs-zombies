package com.pvz.plantsvszombies.Presentation.Entities.Plants;


import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.Plants.WallNutGameObject;
import com.pvz.plantsvszombies.Presentation.Animations.WallNutAnimations;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class WallNutVisualObject extends AbstractPlantVisualObject {


    public enum States {
        FULL_HEALTH,
        CRACKED1,
        CRACKED2
    }

    private WallNutGameObject _gameObject;

    private States _currentState = States.FULL_HEALTH;

    private GeneralFadingAnimation _fadingAnimation;
    private GeneralTransformAnimation _transformAnimation;

    private VisualEngine _engine;

    public WallNutVisualObject(WallNutGameObject gameObject, VisualEngine engine) {
        _engine = engine;

        var temp_this = this;
        gameObject.subscribeToCracked_1_Event(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                System.out.println("wallnut will be full health again");
                changeStateTo(States.CRACKED1);
            }
        });
        gameObject.subscribeToCracked_2_Event(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                System.out.println("wallnut being cracked1");
                changeStateTo(States.CRACKED2);
            }
        });
        gameObject.subscribeToEatenEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                System.out.println("DEAD !");
                _engine.disposeObject(temp_this);
            }
        });

        _visualCoordinate = gameObject.getCoordinate();
        _gameObject = gameObject;
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/WallNut/WallNut_0.png")));

//        _node.setTranslateY(_visualCoordinate.y());
//        _node.setTranslateX(_visualCoordinate.x());


    }

    @Override
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, WallNutAnimations.getFrames((WallNutAnimations.Animations) animation), Duration.millis(80));
    }

    @Override
    public void spawn() {
//        playAnimation(WallNutAnimations.Animations.FULL_HEALTH);
        changeStateTo(States.FULL_HEALTH);
    }

    public WallNutVisualObject changeStateTo(States state) {
        switch (state) {
            case FULL_HEALTH -> {
                _currentState = States.FULL_HEALTH;
                playAnimation(WallNutAnimations.Animations.FULL_HEALTH);
            }
            case CRACKED1 -> {
                if (_currentState.equals(States.FULL_HEALTH)) {
                    _currentState = States.CRACKED1;
                    stopAnimation();
                    playAnimation(WallNutAnimations.Animations.CRACKED1);
                }
            }

            case CRACKED2 -> {
                if (_currentState.equals(States.CRACKED1)) {
                    _currentState = States.CRACKED2;
                    stopAnimation();
                    playAnimation(WallNutAnimations.Animations.CRACKED2);
                }
            }
        }
        return null;
    }
}
