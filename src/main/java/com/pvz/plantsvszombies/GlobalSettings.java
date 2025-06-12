package com.pvz.plantsvszombies;

public class GlobalSettings {
    public static final int FPS = 30;

    public static String getResource(String path) {
        return GlobalSettings.class.getResource("").toString() + path;
    }
}
