package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.IEventSubscriber;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.NormalZombieGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Entities.NormalBulletVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.SkySunVisualObject;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class NormalZombieVisualObject extends AbstractZombieVisualObject {
    public enum States {
        MOVING,
        EATING,
        DYING
    }

    private NormalZombieGameObject _gameObject;
    private VisualEngine _engine;

    private States _currentState;
    private GeneralTransformAnimation _transformAnimation;


    public NormalZombieVisualObject(NormalZombieGameObject gameObject, VisualEngine engine) {
        _gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/NormalZombie/Zombie_0.png")));

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.setManaged(false);
        _node.relocate(_visualCoordinate.x() - 0.3 * width , _visualCoordinate.y() - height * 0.3);

        _currentState = States.MOVING;

        _gameObject.subscribeToMovementEvent((zombieObj) -> {
            _visualCoordinate = zombieObj.getCoordinate();
            Platform.runLater(() -> {
                _node.relocate(_visualCoordinate.x() - 0.3 * width , _visualCoordinate.y() - height * 0.3);
            });
            if (!this._currentState.equals(States.MOVING)) {
                changeStateTo(States.MOVING);
            }
        });

        _gameObject.subscribeToEatingEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    changeStateTo(States.EATING);
                });
            }
        });

        _gameObject.subscribeToDeathEvent((zombieObj) -> {
            _engine.disposeObject(this);
        });
    }

    @Override
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, ZombieAnimations.getFrames((ZombieAnimations.Animations) animation), Duration.millis(90));
    }

    @Override
    public void spawn() {
        playAnimation(ZombieAnimations.Animations.MOVING_FORWARD);
    }

    public NormalZombieVisualObject changeStateTo(NormalZombieVisualObject.States state) {
        switch (state) {
            case MOVING -> {
                _currentState = States.MOVING;
                stopAnimation();
                playAnimation(ZombieAnimations.Animations.MOVING_FORWARD);
            }
            case DYING -> {
                _currentState = States.DYING;
                stopAnimation();
                playAnimation(ZombieAnimations.Animations.DYING);
            }
            case EATING -> {
                _currentState = States.EATING;
                stopAnimation();
//                playAnimation(ZombieAnimations.Animations.ATTACKING);
            }
        }
        return null;
    }
}
