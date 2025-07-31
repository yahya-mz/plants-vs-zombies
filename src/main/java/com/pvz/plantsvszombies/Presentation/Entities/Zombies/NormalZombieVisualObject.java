package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.NormalZombieGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class NormalZombieVisualObject extends AbstractZombieVisualObject {
    public enum States {
        MOVING,
        EATING,
        DYING,
        Burning,
    }

    private final IVisualEngine _engine;

    private States _currentState;
    private GeneralTransformAnimation _transformAnimation;


    public NormalZombieVisualObject(NormalZombieGameObject gameObject, IVisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/NormalZombie/Zombie_0.png")));

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.setManaged(false);
        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - height * 0.5);

        _currentState = States.MOVING;

        ((NormalZombieGameObject) _gameObject).subscribeToMovementEvent((zombieObj) -> {
            _visualCoordinate = zombieObj.getCoordinate();
            Platform.runLater(() -> {
                _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - height * 0.5);
            });
            if (!this._currentState.equals(States.MOVING)) {
                changeStateTo(States.MOVING);
            }
        });

        ((NormalZombieGameObject) _gameObject).subscribeToEatingEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    changeStateTo(States.EATING);
                });
            }
        });

        ((NormalZombieGameObject) _gameObject).subscribeToDeathEvent((zombieObj) -> {//we have to change this
            Platform.runLater(() -> changeStateTo(States.DYING));
        });

        ((NormalZombieGameObject) _gameObject).subscribeToBurnEvent((zombieObj) -> {//we have to change this
            Platform.runLater(() -> changeStateTo(States.Burning));
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
        playAnimation(NormalZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(35));
    }

    public NormalZombieVisualObject changeStateTo(NormalZombieVisualObject.States state) {
        switch (state) {
            case MOVING -> {
                _currentState = States.MOVING;
                playAnimation(NormalZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(35));
            }
            case DYING -> {
                _currentState = States.DYING;
                playAnimation(NormalZombieAnimations.Animations.DYING, Duration.millis(70), 1);
                setOnAnimationFinished(e -> {
                    _engine.disposeObject(this);
                });
            }
            case EATING -> {
                _currentState = States.EATING;
                playAnimation(NormalZombieAnimations.Animations.ATTACKING, Duration.millis(35));
            }
            case Burning -> {
                _currentState = States.Burning;
                playAnimation(NormalZombieAnimations.Animations.BURNING, Duration.millis(70), 1);
                setOnAnimationFinished(e -> {
                    _engine.disposeObject(this);
                });
            }
        }
        return null;
    }
}
