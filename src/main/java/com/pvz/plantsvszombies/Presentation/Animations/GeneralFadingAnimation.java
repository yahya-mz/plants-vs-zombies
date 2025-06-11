package com.pvz.plantsvszombies.Presentation.Animations;

import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractVisualObject;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.ArrayList;

public class GeneralFadingAnimation {

    private Timeline _timeline;

    private static final ArrayList<GeneralFadingAnimation> playingAnimations =
            new ArrayList<>();


    private final AbstractVisualObject _visualObject;

    GeneralFadingAnimation(AbstractVisualObject visualObject) {
        this._visualObject = visualObject;
    }

    public static GeneralFadingAnimation attach(AbstractVisualObject visualObject) {
        return new GeneralFadingAnimation(visualObject);
    }

    public void start() {
        _timeline.playFromStart();
    }

    ;

    public void stop() {
        _timeline.stop();
    }

    public GeneralFadingAnimation fadeOut(Duration duration) {
        var keyFrame = new KeyFrame(duration, new KeyValue(_visualObject.getNode().opacityProperty(), 0));
        _timeline = new Timeline(0, keyFrame);
        _timeline.setCycleCount(1);
        _timeline.playFromStart();
        return this;
    }

    public GeneralFadingAnimation interrupt(){
        _timeline.stop();
        return this;
    }
    public GeneralFadingAnimation setOnFinished(EventHandler<ActionEvent> eventHandler){
        _timeline.setOnFinished(eventHandler);
        return this;
    }
}
