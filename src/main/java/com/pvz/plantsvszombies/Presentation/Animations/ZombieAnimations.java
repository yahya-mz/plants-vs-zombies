package com.pvz.plantsvszombies.Presentation.Animations;

import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ZombieAnimations {
    public enum Animations implements IAnimation {
        IDLE,
        MOVING_FORWARD,
        DYING,
        HITTING,
        ATTACKING
    }

    private final static ArrayList<Image[]> animations;

    static {
        animations = new ArrayList<>();
        var animationsDirectory = new File(ZombieAnimations.class.getResource("animations/zombie_animations").getPath());
        for (int i = 0; i < Animations.values().length; i++) {
            var animationImages = new File(animationsDirectory.getPath() + "/" + Animations.values()[i]).listFiles();
            Objects.requireNonNull(animationImages);
            Image[] animationFrames = new Image[animationImages.length];
            for (int j = 0; j < animationImages.length; j++) {
                animationFrames[j] = new Image(animationImages[j].getPath());
            }
            animations.add(animationFrames);
        }
    }

    public static Image[] getFrames(ZombieAnimations.Animations animation){
        return animations.get(animation.ordinal());
    }
}
