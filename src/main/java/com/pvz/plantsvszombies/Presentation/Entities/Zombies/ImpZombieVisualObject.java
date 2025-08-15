package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.ImpZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.NormalZombieGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundManager;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundType;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Animations.Zombies.ImpZombieAnimations;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ImpZombieVisualObject extends AbstractZombieVisualObject {

    private final VisualEngine _engine;
    private AbstractZombieGameObject.GeneralZombieState _currentVisualState;


    public ImpZombieVisualObject(ImpZombieGameObject gameObject, VisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/ImpZombie/ImpZombie_0.png")));

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

        _gameObject.subscribeToEatingEvent(zombieObj -> {
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
        super.playAnimation(ImpZombieAnimations.getFrames((ImpZombieAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(ImpZombieAnimations.getFrames((ImpZombieAnimations.Animations) animation), frameDuration, cycleCount);
    }

    @Override
    public void spawn() {
        _gameObject.spawn();
        playAnimation(ImpZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(35));
        SoundManager.play(SoundType.IMP_ZOMBIE);
        changeStateTo(_gameObject.getCurrentState());
    }

    public ImpZombieVisualObject changeStateTo(AbstractZombieGameObject.GeneralZombieState state) {
        Platform.runLater(() -> {
            switch (state) {
                case MOVING_FORWARD -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.MOVING_FORWARD;
                    playAnimation(ImpZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(30));
                }
                case FREEZY_MOVING_FORWARD -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.FREEZY_MOVING_FORWARD;
                    playAnimation(ImpZombieAnimations.Animations.FROZEN_MOVING_FORWARD, Duration.millis(60));
                }
                case DYING -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.DYING;
                    playAnimation(ImpZombieAnimations.Animations.DYING, Duration.millis(60), 1);
                    setOnAnimationFinished(e -> {
                        _engine.disposeObject(this);
                    });
                }
                case EATING -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.EATING;
                    playAnimation(ImpZombieAnimations.Animations.ATTACKING, Duration.millis(30));
                }
                case FREEZY_EATING -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.FREEZY_EATING;
                    playAnimation(ImpZombieAnimations.Animations.FROZEN_ATTACKING, Duration.millis(60));
                }
                case BURNING -> {
                    _currentVisualState = AbstractZombieGameObject.GeneralZombieState.BURNING;
                    playAnimation(ImpZombieAnimations.Animations.BURNING, Duration.millis(70), 1);
                    setOnAnimationFinished(e -> {
                        _engine.disposeObject(this);
                    });
                }
            }

        });
        return this;
    }
}
