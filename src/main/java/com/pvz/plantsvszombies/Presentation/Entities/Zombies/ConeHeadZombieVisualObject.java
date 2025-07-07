package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.Zombies.ConeHeadZombieGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ConeHeadZombieVisualObject extends AbstractZombieVisualObject {
    public enum States {
        MOVING,
        EATING,
        LOSTCONE,
        DYING
    }

    private ConeHeadZombieGameObject _gameObject;//chenge this
    private VisualEngine _engine;

    private States _currentState;
    private GeneralTransformAnimation _transformAnimation;


    public ConeHeadZombieVisualObject(ConeHeadZombieGameObject gameObject, VisualEngine engine) {
        _gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/ConeHeadZombie/ConeHeadZombie_0.png")));

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.setManaged(false);
        _node.relocate(_visualCoordinate.x() - 0.3 * width , _visualCoordinate.y() - height * 0.3);

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
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, ConeHeadZombieAnimations.getFrames((ConeHeadZombieAnimations.Animations) animation), Duration.millis(90));
    }

    @Override
    public void spawn() {
        playAnimation(ConeHeadZombieAnimations.Animations.MOVING_FORWARD);
    }

    public ConeHeadZombieVisualObject changeStateTo(ConeHeadZombieVisualObject.States state) {
        switch (state) {
            case MOVING -> {
                _currentState = States.MOVING;
                stopAnimation();
                playAnimation(ConeHeadZombieAnimations.Animations.MOVING_FORWARD);
            }
            case DYING -> {
                _currentState = States.DYING;
                stopAnimation();
                playAnimation(ConeHeadZombieAnimations.Animations.DYING);
            }
            case LOSTCONE -> {
                _currentState = States.EATING;
                stopAnimation();
                playAnimation(ConeHeadZombieAnimations.Animations.LOSTCONE);
            }
            case EATING -> {
                _currentState = States.EATING;
                stopAnimation();
                playAnimation(ConeHeadZombieAnimations.Animations.ATTACKING);
            }
        }
        return null;
    }
}
