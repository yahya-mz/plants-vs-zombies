package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.ConeHeadZombieGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ConeHeadZombieVisualObject extends AbstractZombieVisualObject {
    public enum States {
        MOVING,
        EATING,
        LOST_CONE,
        DYING,
        BURNING,
    }

    private IVisualEngine _engine;

    private States _currentState;
    private GeneralTransformAnimation _transformAnimation;


    public ConeHeadZombieVisualObject(ConeHeadZombieGameObject gameObject, IVisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/ConeHeadZombie/ConeHeadZombie_0.png")));

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.setManaged(false);
        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - height * 0.5);

        _currentState = States.MOVING;

        (_gameObject).subscribeToMovementEvent((zombieObj) -> {
            _visualCoordinate = zombieObj.getCoordinate();
            Platform.runLater(() -> {
                _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - height * 0.5);
            });
            if (!this._currentState.equals(States.MOVING)) {
                changeStateTo(States.MOVING);
            }
        });
        (_gameObject).subscribeToEatingEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    changeStateTo(States.EATING);
                });
            }
        });
        (_gameObject).subscribeToDeathEvent((zombieObj) -> {//we have to change this
            Platform.runLater(() -> changeStateTo(States.DYING));
        });
        _gameObject.subscribeToBurnEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> changeStateTo(States.BURNING));
            }
        });
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(ConeHeadZombieAnimations.getFrames((ConeHeadZombieAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(ConeHeadZombieAnimations.getFrames((ConeHeadZombieAnimations.Animations) animation), frameDuration, cycleCount);
    }

    @Override
    public void spawn() {
        changeStateTo(States.MOVING);
    }

    @Override
    public AbstractZombieGameObject getGameObject() {
        return _gameObject;
    }


    public ConeHeadZombieVisualObject changeStateTo(ConeHeadZombieVisualObject.States state) {
        switch (state) {
            case MOVING -> {
                _currentState = States.MOVING;
                playAnimation(ConeHeadZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(40));
            }
            case DYING -> {
                _currentState = ConeHeadZombieVisualObject.States.DYING;
                playAnimation(ConeHeadZombieAnimations.Animations.DYING, Duration.millis(70), 1);
                setOnAnimationFinished(e -> {
                    _engine.disposeObject(this);
                });
            }
            case LOST_CONE -> {
                _currentState = States.LOST_CONE;
//                playAnimation(ConeHeadZombieAnimations.Animations.LOSTCONE, Duration.millis(90));
            }
            case EATING -> {
                _currentState = States.EATING;
                playAnimation(ConeHeadZombieAnimations.Animations.ATTACKING, Duration.millis(90));
            }
            case BURNING -> {
                _currentState = States.BURNING;
                playAnimation(ConeHeadZombieAnimations.Animations.BURNING, Duration.millis(70), 1);
                setOnAnimationFinished(e -> {
                    _engine.disposeObject(this);
                });
            }
        }
        return null;
    }
}
