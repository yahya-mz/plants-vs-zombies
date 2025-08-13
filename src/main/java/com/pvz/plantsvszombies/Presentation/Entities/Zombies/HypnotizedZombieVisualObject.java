package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.HypnotizedZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralTransformAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.Zombies.HypnotizedZombieAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractAnimatedVisualObject;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class HypnotizedZombieVisualObject extends AbstractAnimatedVisualObject {
    private final VisualEngine _engine;

    private GeneralTransformAnimation _transformAnimation;

    private final HypnotizedZombieGameObject _gameObject;

    public HypnotizedZombieVisualObject(HypnotizedZombieGameObject gameObject, VisualEngine engine) {
        this._gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/HypnotizedZombie/Zombie_0.png")));

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.setManaged(false);
        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - height * 0.5);

        (_gameObject).subscribeToMovementEvent((zombieObj) -> {
            _visualCoordinate = zombieObj.getCoordinate();
            Platform.runLater(() -> {
                _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - height * 0.5);
            });
            if (!_gameObject.getCurrentState().equals(HypnotizedZombieGameObject.HypnotizedZombieStates.MOVING_FORWARD)) {
                changeStateTo(HypnotizedZombieGameObject.HypnotizedZombieStates.MOVING_FORWARD);
            }
        });

        _gameObject.subscribeToEatingEvent(zombieObj -> {
            if (!_gameObject.getCurrentState().equals(HypnotizedZombieGameObject.HypnotizedZombieStates.EATING)) {
                changeStateTo(HypnotizedZombieGameObject.HypnotizedZombieStates.EATING);
            }
        });

        _gameObject.subscribeToDeathEvent((zombieObj) -> {
            Platform.runLater(() -> changeStateTo(HypnotizedZombieGameObject.HypnotizedZombieStates.DYING));
        });

        _gameObject.subscribeToDisposeEvent((zombieObj) -> {
            _engine.disposeObject(this);
        });
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(HypnotizedZombieAnimations.getFrames((HypnotizedZombieAnimations.Animations) animation, _gameObject.getZombieType()), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(HypnotizedZombieAnimations.getFrames((HypnotizedZombieAnimations.Animations) animation, _gameObject.getZombieType()), frameDuration, cycleCount);
    }

    @Override
    public void spawn() {
        _gameObject.spawn();
        playAnimation(HypnotizedZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(35));
    }

    public HypnotizedZombieVisualObject changeStateTo(HypnotizedZombieGameObject.HypnotizedZombieStates state) {
        switch (state) {
            case MOVING_FORWARD -> {
                playAnimation(HypnotizedZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(35));
            }
            case DYING -> {
                playAnimation(HypnotizedZombieAnimations.Animations.DYING, Duration.millis(70), 1);
                setOnAnimationFinished(e -> {
                    _engine.disposeObject(this);
                });
            }
            case EATING -> {
                playAnimation(HypnotizedZombieAnimations.Animations.ATTACKING, Duration.millis(35));
            }
        }
        return this;
    }
}
