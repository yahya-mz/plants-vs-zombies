package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Arrays;

public abstract class AbstractAnimatedVisualObject extends AbstractVisualObject {
    private final Timeline _animationTimeLine;


    AbstractAnimatedVisualObject() {
        this._animationTimeLine = new Timeline();
    }

    public abstract void playAnimation(IAnimation animation);

    protected void playAnimation(IAnimation animation, Image[] frames) {
        if (_animationTimeLine.getStatus().equals(Animation.Status.RUNNING)) {
            stopAnimation();
        }
//        var keyFrames = Arrays.stream(frames)
//                .map(image -> {
//                    return new KeyFrame(Duration.millis(index * 17), (e) -> {
//                        ((ImageView) super.getNode()).setImage(image);
//                    });
//                });

        var keyFrames = new KeyFrame[frames.length];
        for (int i = 0; i < frames.length; i++) {
//            System.out.println(i);
            final int index = i;
            keyFrames[i] = new KeyFrame(Duration.millis(i * 50), (e) -> {
                ((ImageView) super.getNode()).setImage(frames[index]);
            });
        }
        _animationTimeLine.getKeyFrames().setAll(keyFrames);
        _animationTimeLine.setCycleCount(Animation.INDEFINITE);
        _animationTimeLine.setAutoReverse(true);
        _animationTimeLine.playFromStart();
    }

    public void resumeAnimation() {
        if (_animationTimeLine.getStatus().equals(Animation.Status.PAUSED)) {
            _animationTimeLine.play();
        }
    }

    public void stopAnimation() {
        _animationTimeLine.stop();
    }
}
