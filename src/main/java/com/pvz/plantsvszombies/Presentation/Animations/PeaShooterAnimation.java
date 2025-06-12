package com.pvz.plantsvszombies.Presentation.Animations;

import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class PeaShooterAnimation {
    public enum Animations implements IAnimation {
        SHOOTING,
        STANDING
    }

    private static final ArrayList<Image[]> animations;

    static {
        animations = new ArrayList<>();
        var animationsDirectory = new File(SunAnimations.class.getResource("/com/pvz/plantsvszombies/graphics/Plants/Peashooter").getPath());
        for (int i = 0; i < SunAnimations.Animations.values().length; i++) {
            var animationImages = new File(animationsDirectory.getPath() + "/" + SunAnimations.Animations.values()[i].name()).listFiles();
            Objects.requireNonNull(animationImages);
            Image[] animationFrames = new Image[animationImages.length];
            for (int j = 0; j < animationImages.length; j++) {
                animationFrames[j] = new Image(animationImages[j].getPath());
            }
            animations.add(animationFrames);
        }
    }

    public static Image[] getFrames(PeaShooterAnimation.Animations animation) {
        return animations.get(animation.ordinal());
    }
}