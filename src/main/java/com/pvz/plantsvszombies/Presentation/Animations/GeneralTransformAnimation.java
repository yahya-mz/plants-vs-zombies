package com.pvz.plantsvszombies.Presentation.Animations;

import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractVisualObject;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;

public class GeneralTransformAnimation {

    private Timeline _timeline;

    private static final ArrayList<GeneralTransformAnimation> playingAnimations =
            new ArrayList<>();


    private final AbstractVisualObject _visualObject;

    GeneralTransformAnimation(AbstractVisualObject visualObject) {
        this._visualObject = visualObject;
    }

    public static GeneralTransformAnimation attach(AbstractVisualObject visualObject) {
        return new GeneralTransformAnimation(visualObject);
    }

    public void start() {
        _timeline.playFromStart();
    }

    public void interrupt() {
        _timeline.stop();
    }

    public GeneralTransformAnimation transformY(double speed, double distance) {
        var keyFrame = new KeyFrame(Duration.millis(Math.abs(distance / speed) / GlobalSettings.FPS * 1000), new KeyValue(_visualObject.getNode().translateYProperty(), _visualObject.getNode().getTranslateY() +
                speed / Math.abs(speed) * distance));
        _timeline = new Timeline(0, keyFrame);
        _timeline.setCycleCount(1);
        _timeline.playFromStart();
        return this;
    }

    public GeneralTransformAnimation transformX(double speed, double distance) {
        _timeline = new Timeline(0, new KeyFrame(Duration.millis(1000.0 / GlobalSettings.FPS), (e) -> {
            _visualObject.getNode().setTranslateX(_visualObject.getNode().getTranslateY() + speed);
        }));
        return this;
    }

    public GeneralTransformAnimation transform(double horizontalSpeed, double verticalSpeed, double horizontalDistance, double VerticalDistance) {
        var horizontalKeyFrame = new KeyFrame(Duration.millis(Math.abs(horizontalDistance / horizontalSpeed) / GlobalSettings.FPS * 1000), new KeyValue(_visualObject.getNode().translateXProperty(), _visualObject.getNode().getTranslateY() +
                horizontalSpeed / Math.abs(horizontalSpeed) * horizontalDistance));
        var verticalKeyFrame = new KeyFrame(Duration.millis(Math.abs(VerticalDistance / verticalSpeed) / GlobalSettings.FPS * 1000), new KeyValue(_visualObject.getNode().translateYProperty(), _visualObject.getNode().getTranslateY() +
                verticalSpeed / Math.abs(verticalSpeed) * VerticalDistance));
        _timeline = new Timeline(0, horizontalKeyFrame, verticalKeyFrame);
        _timeline.setCycleCount(1);
        _timeline.playFromStart();
        return this;
    }
}
