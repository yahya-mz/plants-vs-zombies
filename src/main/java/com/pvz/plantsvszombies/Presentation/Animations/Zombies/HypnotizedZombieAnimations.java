package com.pvz.plantsvszombies.Presentation.Animations.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class HypnotizedZombieAnimations {
    public enum Animations implements IAnimation {
        MOVING_FORWARD,
        DYING,
        ATTACKING,
    }

    private final static ArrayList<Image[]> _hypnoNormalAnimations;
    private final static ArrayList<Image[]> _hypnoScreenDoorAnimations;
    private final static ArrayList<Image[]> _hypnoImpAnimations;
    private final static ArrayList<Image[]> _hypnoConeHeadAnimations;

    static {
        _hypnoNormalAnimations = new ArrayList<>();
        loadAnimations(new File(GlobalSettings.getDir("graphics/Zombies/HypnoNormalZombie")), _hypnoNormalAnimations);

        _hypnoScreenDoorAnimations = new ArrayList<>();
        loadAnimations(new File(GlobalSettings.getDir("graphics/Zombies/HypnoScreenDoorZombie")), _hypnoScreenDoorAnimations);

        _hypnoImpAnimations = new ArrayList<>();
        loadAnimations(new File(GlobalSettings.getDir("graphics/Zombies/HypnoImpZombie")), _hypnoImpAnimations);

        _hypnoConeHeadAnimations = new ArrayList<>();
        loadAnimations(new File(GlobalSettings.getDir("graphics/Zombies/HypnoConeHeadZombie")), _hypnoConeHeadAnimations);


    }

    public static Image[] getFrames(Animations animation, AbstractZombieGameObject.ZombieType zombieType) {
        return switch (zombieType) {
            case NORMAL_ZOMBIE -> _hypnoNormalAnimations.get(animation.ordinal());
            case CONE_HEAD_ZOMBIE -> _hypnoConeHeadAnimations.get(animation.ordinal());
            case SCREEN_DOOR_ZOMBIE -> _hypnoScreenDoorAnimations.get(animation.ordinal());
            case IMP_ZOMBIE -> _hypnoImpAnimations.get(animation.ordinal());
            default -> null;
        };
    }

    private static void loadAnimations(File animationsDirectory, ArrayList<Image[]> animations) {
        for (int i = 0; i < Animations.values().length; i++) {
            var animationImages = new File(animationsDirectory.getPath() + "/" + Animations.values()[i].name()).listFiles();
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
}
