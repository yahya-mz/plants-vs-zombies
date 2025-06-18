package com.pvz.plantsvszombies;

import java.util.Objects;

public class GlobalSettings {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 728;


    public static final int FPS = 30;

    public static String getResource(String path) {
        try{
            return Objects.requireNonNull(GlobalSettings.class.getResource(path)).toString();
        }catch (NullPointerException ex){
            System.out.println("The requested resource doesn't exist: "+ex.getMessage());
        }
        return "";
    }
    public static String getDir(String path) {
        try{
            return Objects.requireNonNull(GlobalSettings.class.getResource(path)).getPath();
        }catch (NullPointerException ex){
            System.out.println("The requested resource doesn't exist: "+ex.getMessage());
        }
        return "";
    }
}
