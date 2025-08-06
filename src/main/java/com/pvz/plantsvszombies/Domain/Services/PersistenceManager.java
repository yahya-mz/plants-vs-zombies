package com.pvz.plantsvszombies.Domain.Services;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.FogGameObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class PersistenceManager {
    public static void saveAsJson(List<AbstractGameObject> gameObjects) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerSubtypes(FogGameObject.class);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        //        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        try {
            objectMapper.writeValue(new File("target/export.json"), gameObjects);
        } catch (Exception e) {
            System.out.println("Exception on saving: " + e.getMessage());
        }

        try {
            var objects = objectMapper.readValue(new File("target/export.json"), new TypeReference<List<AbstractGameObject>>() {
            });
            var deo2 = 3;
        } catch (Exception e) {
            var haj = 2;
        }
    }

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
