package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Presentation.Animations.SunAnimations;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class FlowerSunVisualObject extends AbstractPlantVisualObject {

    private Coordinate _coordinate;
    ImageView _imageView;

    FlowerSunVisualObject() {
//        _animationTimeLine.setRate(20);
    }

    @Override
    public void playAnimation(IAnimation animation) {
        super.playAnimation(animation, SunAnimations.getFrames((SunAnimations.Animations) animation), Duration.millis(80));
    }

    @Override
    public void spawn() {

    }
}
