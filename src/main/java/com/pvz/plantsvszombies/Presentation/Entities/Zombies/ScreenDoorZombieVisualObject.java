package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.ScreenDoorZombieGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.Zombies.ScreenDoorZombieAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ScreenDoorZombieVisualObject extends AbstractZombieVisualObject {

    private final VisualEngine _engine;
    private AbstractZombieGameObject.GeneralZombieState _currentVisualState;

    public ScreenDoorZombieVisualObject(ScreenDoorZombieGameObject gameObject, VisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/ScreenDoorZombie/ScreenDoorZombie_0.png")));

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.setManaged(false);
        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - height * 0.5);

        _currentVisualState = _gameObject.getCurrentState();

        (_gameObject).subscribeToMovementEvent((zombieObj) -> {
            _visualCoordinate = zombieObj.getCoordinate();
            Platform.runLater(() -> {
                _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - height * 0.5);
            });
            if (!_currentVisualState.equals(AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD)) {
                changeStateTo(AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD);
            }
        });

        _gameObject.subscribeToEatingEvent(zombieObj ->
        {
            if (!_gameObject.getCurrentState().equals(AbstractZombieGameObject.GeneralZombieState.EATING)) {
                changeStateTo(AbstractZombieGameObject.GeneralZombieState.EATING);
            }
        });

        _gameObject.subscribeToDeathEvent((zombieObj) -> {
            changeStateTo(AbstractZombieGameObject.GeneralZombieState.DYING);
        });

        _gameObject.subscribeToBurnEvent((zombieObj) -> {
            changeStateTo(AbstractZombieGameObject.GeneralZombieState.BURNING);
        });

        _gameObject.subscribeToDisposeEvent((zombieObj) -> {
            _engine.disposeObject(this);
        });

    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(ScreenDoorZombieAnimations.getFrames((ScreenDoorZombieAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(ScreenDoorZombieAnimations.getFrames((ScreenDoorZombieAnimations.Animations) animation), frameDuration, cycleCount);
    }

    @Override
    public void spawn() {
        _gameObject.spawn();
        changeStateTo(_gameObject.getCurrentState());
    }

    public ScreenDoorZombieVisualObject changeStateTo(AbstractZombieGameObject.GeneralZombieState state) {
        Platform.runLater(() -> {
            switch (state) {
                case MOVING_FORWARD -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD;
                    playAnimation(ScreenDoorZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(30));
                }
                case DYING -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.DYING;
                    playAnimation(ScreenDoorZombieAnimations.Animations.DYING, Duration.millis(60), 1);
                    setOnAnimationFinished(e -> {
                        Platform.runLater(() -> {
                            _engine.disposeObject(this);
                        });
                    });
                }
                case EATING -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.EATING;
                    playAnimation(ScreenDoorZombieAnimations.Animations.ATTACKING, Duration.millis(30));
                }
                case BURNING -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.BURNING;
                    playAnimation(ScreenDoorZombieAnimations.Animations.BURNING, Duration.millis(70), 1);
                    setOnAnimationFinished(e -> {
                        _engine.disposeObject(this);
                    });
                }
            }
        });
        return this;
    }
}
