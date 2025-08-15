package com.pvz.plantsvszombies.Presentation.Entities.Plants;


import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.TallNutGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.TallnutAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class TallNutVisualObject extends AbstractPlantVisualObject {

    private final VisualEngine _engine;

    public TallNutVisualObject(TallNutGameObject gameObject, VisualEngine engine) {
        _visualCoordinate = gameObject.getCoordinate();
        _gameObject = gameObject;
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/TallNut/TallNut_0.png")));
        _engine = engine;

        var temp_this = this;
        gameObject.subscribeToCracked_1_Event(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                changeStateTo(TallNutGameObject.TallNutStates.CRACKED1);
            }
        });
        gameObject.subscribeToCracked_2_Event(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                changeStateTo(TallNutGameObject.TallNutStates.CRACKED2);
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
        super.playAnimation(TallnutAnimations.getFrames((TallnutAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        changeStateTo(((TallNutGameObject) _gameObject).getCurrentState());
    }

    public TallNutVisualObject changeStateTo(TallNutGameObject.TallNutStates state) {
        switch (state) {
            case FULL_HEALTH -> {
                playAnimation(TallnutAnimations.Animations.FULL_HEALTH, Duration.millis(80));
            }
            case CRACKED1 -> {
                stopAnimation();
                playAnimation(TallnutAnimations.Animations.CRACKED1, Duration.millis(80));
            }

            case CRACKED2 -> {
                stopAnimation();
                playAnimation(TallnutAnimations.Animations.CRACKED2, Duration.millis(80));
            }
        }
        return this;
    }
}
