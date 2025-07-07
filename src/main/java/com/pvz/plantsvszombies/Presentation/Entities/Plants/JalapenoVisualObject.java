package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.Plants.CherryBombGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.JalapenoGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.*;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class JalapenoVisualObject extends AbstractPlantVisualObject {
    public enum States {
        BLOWING
    }

    private JalapenoGameObject _gameObject;

    private States _currentState;

    private VisualEngine _engine;

    public JalapenoVisualObject(JalapenoGameObject gameObject, VisualEngine engine) {//وابستگی ها و مقدار دهی  change object
        _gameObject = gameObject;
        _engine = engine;

//        gameObject.subscribeToBlowingEvent(new IEventSubscriber() {//notify//we dont need this//بهتر نیست انیمیشن رو در اینجا تعریف کنیم؟؟؟
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                Platform.runLater(() -> {
//                    System.out.println("Blowing: " + gameObject.getCoordinate().x() + "," + gameObject.getCoordinate().y());
//                    changeStateTo(States.BLOWING);
//                });
//            }
//        });

        var temp_this = this;
//        gameObject.subscribeToEatenEvent(new IEventSubscriber() {//after spawning it will be eaten
//            @Override
//            public void _notify(AbstractGameObject gameObject) {
//                stopAnimation();
//                _engine.disposeObject(temp_this);
//            }
//        });

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/Jalapeno/Jalapeno_0.png")));
//        _node.setTranslateY(_visualCoordinate.y());
//        _node.setTranslateX(_visualCoordinate.x());

    }

    @Override
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, JalapenoAnimations.getFrames((JalapenoAnimations.Animations) animation), Duration.millis(90));
    }

    @Override
    public void spawn() {
        Platform.runLater(() -> {
            System.out.println("demodemo");
//            playAnimation(JalapenoAimations.Animations.BLOWING);//standing//the animation is not inf is one time only
            changeStateTo(States.BLOWING);

        });
    }

    public JalapenoVisualObject changeStateTo(States state) {//useless
        switch (state) {
            case BLOWING -> {
                _currentState = States.BLOWING;
            }
        }
        return null;
    }
}
