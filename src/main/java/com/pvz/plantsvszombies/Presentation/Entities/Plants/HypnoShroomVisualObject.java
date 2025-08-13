package com.pvz.plantsvszombies.Presentation.Entities.Plants;


import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.HypnoShroomGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.HypnoShroomAnimations;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class HypnoShroomVisualObject extends AbstractPlantVisualObject {

    public enum States {
        SLEEPING,
        STANDING
    }

    private States _currentState;

    private final VisualEngine _engine;

    public HypnoShroomVisualObject(HypnoShroomGameObject gameObject, VisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _gameObject = gameObject;
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/HypnoShroom/HypnoShroom_0.png")));

        if (gameObject.isAwake()) {
            _currentState = States.STANDING;
        } else {
            _currentState = States.SLEEPING;
        }

        var temp_this = this;
        gameObject.subscribeToEatenEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                _engine.disposeObject(temp_this);
            }
        });
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(HypnoShroomAnimations.getFrames((HypnoShroomAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        playAnimation(HypnoShroomAnimations.Animations.STANDING, Duration.millis(80));
    }

    public HypnoShroomVisualObject changeStateTo(States state) {
        switch (state) {

        }
        return null;
    }
}
