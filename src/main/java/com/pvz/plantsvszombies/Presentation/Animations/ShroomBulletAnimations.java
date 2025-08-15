package com.pvz.plantsvszombies.Presentation.Animations;

import com.pvz.plantsvszombies.GlobalSettings;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class ShroomBulletAnimations {
        public enum Animations implements IAnimation {
            MOVING,
            COLLIDED
        }

        private static final ArrayList<Image[]> animations;

        static {
        animations = new ArrayList<>();
        var animationsDirectory = new File(GlobalSettings.getDir("graphics/Bullets/ShroomBullet"));
        for (int i = 0; i < ShroomBulletAnimations.Animations.values().length; i++) {
            var animationImages = new File(animationsDirectory.getPath() + "/" + ShroomBulletAnimations.Animations.values()[i].name()).listFiles();
            Arrays.sort(animationImages, Comparator.comparingInt(f -> {
                String name = f.getName();
                int dotIndex = name.lastIndexOf('.');
                if (dotIndex != -1) {
                    name = name.substring(0, dotIndex); // remove file extension
                }
                return Integer.parseInt(name); // assume name is a number
            }));
            Objects.requireNonNull(animationImages);
            Image[] animationFrames = new Image[animationImages.length];
            for (int j = 0; j < animationImages.length; j++) {
                animationFrames[j] = new Image(animationImages[j].getPath());
            }
            animations.add(animationFrames);
        }
    }

        public static Image[] getFrames(ShroomBulletAnimations.Animations animation) {
        return animations.get(animation.ordinal());
    }
}
