package com.pvz.plantsvszombies.Presentation.Animations;

import com.pvz.plantsvszombies.Presentation.Entities.WallnutVisualObject;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class WallnutAnimations {
    public enum Animations implements IAnimation {
        STANDING,
        CRACKED1,
        CRACKED2
    }

    private final static ArrayList<Image[]> animations;

    static {
        animations = new ArrayList<>();
        var animationsDirectory = new File(WallnutVisualObject.class.getResource("graphics/Plants/WallNut").getPath());
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

    public static Image[] getFrames(WallnutAnimations.Animations animation){
        return animations.get(animation.ordinal());
    }
}
