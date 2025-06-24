package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

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

    private NormalBulletVisualObject.States _currentState;
    private GeneralTransformAnimation _transformAnimation;


    public NormalZombieVisualObject(NormalZombieGameObject gameObject, VisualEngine engine) {
        _gameObject = gameObject;
        _engine = engine;
        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Zombies/NormalZombie/Zombie_0.png")));

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        _node.setManaged(false);
        _node.relocate(_visualCoordinate.x(), _visualCoordinate.y() - height * 0.3);

        _gameObject.subscribeToMovementEvent((zombieObj) -> {
            _visualCoordinate = zombieObj.getCoordinate();
//            System.out.println(_visualCoordinate.x());
            Platform.runLater(() -> {
                _node.relocate(_visualCoordinate.x(), _visualCoordinate.y() - height * 0.3);
            });
        });

        _gameObject.subscribeToDeathEvent((zombieObj)->{
            _engine.disposeObject(this);
        });
    }

    @Override
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, ZombieAnimations.getFrames((ZombieAnimations.Animations) animation), Duration.millis(90));
    }

    @Override
    public void spawn() {
        playAnimation(ZombieAnimations.Animations.MOVING_FORWARD);//standing
    }

    public NormalZombieVisualObject changeStateTo(NormalZombieVisualObject.States state) {
        switch (state) {
            case MOVING -> {
                playAnimation(ZombieAnimations.Animations.MOVING_FORWARD);
            }
            case DYING -> {
                stopAnimation();
                playAnimation(ZombieAnimations.Animations.DYING);
            }
//            case FADING_OUT -> {
//                _currentState = SkySunVisualObject.States.FADING_OUT;
//                _fadingAnimation = GeneralFadingAnimation.attach(this).fadeOut(Duration.millis(2500))
//                        .setOnFinished((e) -> {
////                            ((Pane) this.getNode().getParent()).getChildren().remove(_node);
//                            _engine.disposeObject(this);
//                            _gameObject.dispose();
//                        });
//            }
        }
        return null;
    }
}
