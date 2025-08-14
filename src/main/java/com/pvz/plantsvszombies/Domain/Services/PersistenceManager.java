package com.pvz.plantsvszombies.Domain.Services;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenceManager {
    public static void saveAsDat(List<AbstractGameObject> gameObjects) {
        // Save game
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("save.dat")
        )) {
            oos.writeObject(gameObjects);  // Your List<AbstractGameObject>
        } catch (Exception e) {
            System.out.println("Exception on saving");
        }
    }

    public static List<AbstractGameObject> load() {
        List<AbstractGameObject> objects = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("save.dat")
        )) {
            objects = (List<AbstractGameObject>) ois.readObject();

        } catch (Exception e) {
            System.out.println("Exception on loading");

        }
        return objects;
    }
}
