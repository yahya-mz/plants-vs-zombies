package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.ImpZombieGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ImpZombieVisualObject extends AbstractZombieVisualObject {
    public enum States {
        MOVING_FORWARD,
        ATTACKING,
        DYING,
        Burning,
    }

    private final IVisualEngine _engine;

    private States _currentState;
    private GeneralTransformAnimation _transformAnimation;


    public ImpZombieVisualObject(ImpZombieGameObject gameObject, IVisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/ImpZombie/ImpZombie_0.png")));

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.setManaged(false);
        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - height * 0.5);

        _currentState = States.MOVING_FORWARD;

        ((ImpZombieGameObject) _gameObject).subscribeToMovementEvent((zombieObj) -> {
            _visualCoordinate = zombieObj.getCoordinate();
            Platform.runLater(() -> {
                _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - height * 0.5);
            });
            if (!this._currentState.equals(States.MOVING_FORWARD)) {
                changeStateTo(States.MOVING_FORWARD);
            }
        });

        ((ImpZombieGameObject) _gameObject).subscribeToEatingEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    changeStateTo(States.ATTACKING);
                });
            }
        });

        ((ImpZombieGameObject) _gameObject).subscribeToDeathEvent((zombieObj) -> {//we have to change this
            Platform.runLater(() -> changeStateTo(States.DYING));
        });

        ((ImpZombieGameObject) _gameObject).subscribeToBurnEvent((zombieObj) -> {//we have to change this
            Platform.runLater(() -> changeStateTo(States.Burning));
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
    }

    @Override
    public AbstractZombieGameObject getGameObject() {
        return _gameObject;
    }

    public ImpZombieVisualObject changeStateTo(ImpZombieVisualObject.States state) {
        switch (state) {
            case MOVING_FORWARD -> {
                _currentState = States.MOVING_FORWARD;
                playAnimation(ImpZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(35));
            }
            case DYING -> {
                _currentState = States.DYING;
                playAnimation(ImpZombieAnimations.Animations.DYING, Duration.millis(35), 1);
                setOnAnimationFinished(e -> {
                    _engine.disposeObject(this);
                });
            }
            case ATTACKING -> {
                _currentState = States.ATTACKING;
                playAnimation(ImpZombieAnimations.Animations.ATTACKING, Duration.millis(35));
            }
            case Burning -> {
                _currentState = States.Burning;
                playAnimation(ImpZombieAnimations.Animations.BURNING, Duration.millis(35), 1);
                setOnAnimationFinished(e -> {
                    _engine.disposeObject(this);
                });
            }
        }
        return null;
    }
}
