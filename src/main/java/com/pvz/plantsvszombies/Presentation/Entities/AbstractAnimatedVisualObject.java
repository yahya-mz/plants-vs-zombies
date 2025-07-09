package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public abstract class AbstractAnimatedVisualObject extends AbstractVisualObject {
    private final Timeline _animationTimeLine;


    public AbstractAnimatedVisualObject() {
        this._animationTimeLine = new Timeline();
    }

    protected void playAnimation(Image[] frames, Duration frameDuration, int cycleCount) {
        if (_animationTimeLine.getStatus().equals(Animation.Status.RUNNING)) {
            stopAnimation();
            _animationTimeLine.getKeyFrames().clear();
        }
//        var keyFrames = Arrays.stream(frames)
//                .map(image -> {
//                    return new KeyFrame(Duration.millis(index * 17), (e) -> {
//                        ((ImageView) super.getNode()).setImage(image);
//                    });
//                });

//        var keyFrames = new KeyFrame[frames.length];
//        for (int i = 0; i < frames.length; i++) {
//            System.out.println(i);
//            final int index = i;
//            keyFrames[i] = new KeyFrame(frameDuration.multiply(i), new KeyValue(((ImageView) super.getNode()).imageProperty(), frames[index]));
//            keyFrames[i] = new KeyFrame(frameDuration.multiply(i), (e) -> {
//                ((ImageView) super.getNode()).setImage();
//            });
//        }
//        _animationTimeLine.getKeyFrames().setAll(keyFrames);
//        _animationTimeLine.setCycleCount(Animation.INDEFINITE);
//        _animationTimeLine.setAutoReverse(true);
//        _animationTimeLine.setOnFinished((e) -> {
//            _animationTimeLine.playFromStart();
//        });
//        _animationTimeLine.playFromStart();


        // With TimeLine:

        var node = (ImageView) super.getNode();
//        Timer timer = new Timer();
//        timer.schedule(
//                new TimerTask() {
//                    int index = 0;
//                    @Override
//                    public void run() {
//                        Platform.runLater(() -> {
//                            node.setImage(frames[index]);
//                        });
//                        index++;
//                        if (index== frames.length){
//                            index=1;
//                        }
//                    }
//                }
//                , 0, (long) frameDuration.toMillis()
//        );
        int[] index = {0}; // Using array to allow modification in lambda
        _animationTimeLine.getKeyFrames().add(
                new KeyFrame(
                        Duration.millis(frameDuration.toMillis()),
                        event -> {
                            Platform.runLater(() -> {
                                node.setImage(frames[index[0]]);
                            });
                            index[0]++;
                            if (index[0] == frames.length) {
                                index[0] = 1;
                            }
                        }
                )
        );
        _animationTimeLine.setCycleCount(cycleCount == Timeline.INDEFINITE ? Timeline.INDEFINITE : cycleCount * frames.length);
        _animationTimeLine.play();
    }

    protected void playAnimation(Image[] frames, Duration frameDuration) {
        playAnimation(frames, frameDuration, Timeline.INDEFINITE);
    }

    public abstract void playAnimation(IAnimation animation, Duration frameDuration);

    public void resumeAnimation() {
        if (_animationTimeLine.getStatus().equals(Animation.Status.PAUSED)) {
            _animationTimeLine.play();
        }
    }

    public void stopAnimation() {
        _animationTimeLine.stop();
    }

    public void setOnAnimationFinished(EventHandler<ActionEvent> eventHandler) {
        _animationTimeLine.setOnFinished(eventHandler);
    }

    protected void addFrames(Image[] frames, Duration frameDuration) {

        var node = (ImageView) super.getNode();

        int[] index = {0}; // Using array to allow modification in lambda
        _animationTimeLine.getKeyFrames().add(
                new KeyFrame(
                        Duration.millis(frameDuration.toMillis()),
                        event -> {
                            Platform.runLater(() -> {
                                node.setImage(frames[index[0]]);
                            });
                            index[0]++;
                            if (index[0] == frames.length) {
                                index[0] = 1;
                            }
                        }
                )
        );
    }
}
