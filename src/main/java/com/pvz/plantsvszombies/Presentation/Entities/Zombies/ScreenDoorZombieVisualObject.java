package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.Zombies.ScreenDoorZombieGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ScreenDoorZombieVisualObject extends AbstractZombieVisualObject {
    public enum States {
        MOVING,
        EATING,
        LOSTDOOR,
        DYING
    }

    private ScreenDoorZombieGameObject _gameObject;
    private IVisualEngine _engine;

    private States _currentState;
    private GeneralTransformAnimation _transformAnimation;


    public ScreenDoorZombieVisualObject(ScreenDoorZombieGameObject gameObject, IVisualEngine engine) {
        _gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/ScreenDoorZombie/Zombie_0.png")));

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.setManaged(false);
        _node.relocate(_visualCoordinate.x() - 0.3 * width, _visualCoordinate.y() - height * 0.3);

        _currentState = States.MOVING;

//        _gameObject.subscribeToMovementEvent((zombieObj) -> {
//            _visualCoordinate = zombieObj.getCoordinate();
//            Platform.runLater(() -> {
//                _node.relocate(_visualCoordinate.x() - 0.3 * width , _visualCoordinate.y() - height * 0.3);
//            });
//            if (!this._currentState.equals(States.MOVING)) {
//                changeStateTo(States.MOVING);
//            }
//        });
//
//        _gameObject.subscribeToEatingEvent(new IEventSubscriber() {
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                Platform.runLater(() -> {
//                    changeStateTo(States.EATING);
//                });
//            }
//        });
//
//        _gameObject.subscribeToDeathEvent((zombieObj) -> {//we have to change this
//            Platform.runLater(() -> changeStateTo(States.DYING));
//        });
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(ScreenDoorZombieAnimations.getFrames((ScreenDoorZombieAnimations.Animations) animation), frameDuration);
    }

    @Override
    public void spawn() {
        playAnimation(ScreenDoorZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(90));
    }

    public ScreenDoorZombieVisualObject changeStateTo(ScreenDoorZombieVisualObject.States state) {
        switch (state) {
            case MOVING -> {
                _currentState = States.MOVING;
                stopAnimation();
                playAnimation(ScreenDoorZombieAnimations.Animations.MOVING_FORWARD, Duration.millis(90));
            }
            case DYING -> {
                _currentState = States.DYING;
                stopAnimation();
                playAnimation(ScreenDoorZombieAnimations.Animations.DYING, Duration.millis(90));
            }
            case LOSTDOOR -> {
                _currentState = States.LOSTDOOR;
                stopAnimation();
                playAnimation(ScreenDoorZombieAnimations.Animations.LOSTDOOR, Duration.millis(90));
            }
            case EATING -> {
                _currentState = States.EATING;
                stopAnimation();
                playAnimation(ScreenDoorZombieAnimations.Animations.ATTACKING, Duration.millis(90));
            }
        }
        return null;
    }
}
