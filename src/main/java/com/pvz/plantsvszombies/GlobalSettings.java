package com.pvz.plantsvszombies;


import java.util.Objects;
import java.net.URL;

public class GlobalSettings {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 728;


    public static final int FPS = 30;

    public static String getResource(String path) {
        try {
            URL resource = GlobalSettings.class.getResource(path);
            if (resource != null) {
                return resource.toExternalForm();
            } else {
                System.err.println("Resource not found: " + path);
                return "";
            }
        } catch (Exception ex) {
            System.err.println("Error loading resource " + path + ": " + ex.getMessage());
            return "";
        }
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
