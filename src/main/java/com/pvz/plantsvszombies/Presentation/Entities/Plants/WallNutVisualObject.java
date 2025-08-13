package com.pvz.plantsvszombies.Presentation.Entities.Plants;


import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.WallNutGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.WallNutAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class WallNutVisualObject extends AbstractPlantVisualObject {

    private final VisualEngine _engine;

    public WallNutVisualObject(WallNutGameObject gameObject, VisualEngine engine) {
        _visualCoordinate = gameObject.getCoordinate();
        _gameObject = gameObject;
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/WallNut/WallNut_0.png")));
        _engine = engine;

        var temp_this = this;
        gameObject.subscribeToCracked_1_Event(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                changeStateTo(WallNutGameObject.WallNutStates.CRACKED1);
            }
        });
        gameObject.subscribeToCracked_2_Event(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                changeStateTo(WallNutGameObject.WallNutStates.CRACKED2);
            }
        });
        gameObject.subscribeToEatenEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                _engine.disposeObject(temp_this);
            }
        });


//        _node.setTranslateY(_visualCoordinate.y());
//        _node.setTranslateX(_visualCoordinate.x());


    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(WallNutAnimations.getFrames((WallNutAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        changeStateTo(((WallNutGameObject) _gameObject).getCurrentState());
    }

    public WallNutVisualObject changeStateTo(WallNutGameObject.WallNutStates state) {
        switch (state) {
            case FULL_HEALTH -> {
                playAnimation(WallNutAnimations.Animations.FULL_HEALTH, Duration.millis(80));
            }
            case CRACKED1 -> {
                stopAnimation();
                playAnimation(WallNutAnimations.Animations.CRACKED1, Duration.millis(80));
            }

            case CRACKED2 -> {
                stopAnimation();
                playAnimation(WallNutAnimations.Animations.CRACKED2, Duration.millis(80));
            }
        }
        return this;
    }
}
