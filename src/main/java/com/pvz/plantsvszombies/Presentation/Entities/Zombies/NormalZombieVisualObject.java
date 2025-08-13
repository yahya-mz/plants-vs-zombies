package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.NormalZombieGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Animations.Zombies.NormalZombieAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class NormalZombieVisualObject extends AbstractZombieVisualObject {

    private final VisualEngine _engine;
    private AbstractZombieGameObject.GeneralZombieState _currentVisualState;

    public NormalZombieVisualObject(NormalZombieGameObject gameObject, VisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/NormalZombie/Zombie_0.png")));

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
            if (_gameObject.isFreezy()) {
                if (!_currentVisualState.equals(AbstractZombieGameObject.GeneralZombieState.FREEZY_MOVING_FORWARD)) {
                    changeStateTo(AbstractZombieGameObject.GeneralZombieState.FREEZY_MOVING_FORWARD);
                }
            } else if (!_currentVisualState.equals(AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD)) {
                changeStateTo(AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD);
            }
        });

        _gameObject.subscribeToEatingEvent(zombieObj ->
        {
            if (_gameObject.isFreezy()) {
                if (!_currentVisualState.equals(AbstractZombieGameObject.GeneralZombieState.FREEZY_EATING)) {
                    changeStateTo(AbstractZombieGameObject.GeneralZombieState.FREEZY_EATING);
                }
            } else if (!_currentVisualState.equals(AbstractZombieGameObject.GeneralZombieState.EATING)) {
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
        super.playAnimation(NormalZombieAnimations.getFrames((NormalZombieAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(NormalZombieAnimations.getFrames((NormalZombieAnimations.Animations) animation), frameDuration, cycleCount);
    }

    @Override
    public void spawn() {
        _gameObject.spawn();
        changeStateTo(_gameObject.getCurrentState());
    }

    public NormalZombieVisualObject changeStateTo(AbstractZombieGameObject.GeneralZombieState state) {
        Platform.runLater(() -> {
            switch (state) {
                case MOVING_FORWARD -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD;
                    playAnimation(NormalZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(30));
                }
                case FREEZY_MOVING_FORWARD -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.FREEZY_MOVING_FORWARD;
                    playAnimation(NormalZombieAnimations.Animations.FROZEN_MOVING_FORWARD, Duration.millis(60));
                }
                case DYING -> {
                    System.out.println("demdeomdeomoed");
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.DYING;
                    playAnimation(NormalZombieAnimations.Animations.DYING, Duration.millis(60), 1);
                    setOnAnimationFinished(e -> {
                        Platform.runLater(() -> {
                            _engine.disposeObject(this);
                        });
                    });
                }
                case EATING -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.EATING;
                    playAnimation(NormalZombieAnimations.Animations.ATTACKING, Duration.millis(30));
                }
                case FREEZY_EATING -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.FREEZY_EATING;
                    playAnimation(NormalZombieAnimations.Animations.FROZEN_ATTACKING, Duration.millis(60));
                }
                case BURNING -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.BURNING;
                    playAnimation(NormalZombieAnimations.Animations.BURNING, Duration.millis(70), 1);
                    setOnAnimationFinished(e -> {
                        _engine.disposeObject(this);
                    });
                }
            }

        });
        return this;
    }
}
