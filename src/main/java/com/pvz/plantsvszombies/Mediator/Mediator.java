package com.pvz.plantsvszombies.Mediator;

import com.pvz.plantsvszombies.Domain.Entities.Events.*;
import com.pvz.plantsvszombies.Presentation.EventHandlers.*;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Platform;

import javax.print.attribute.standard.Media;
import java.util.Hashtable;

public class Mediator {

//    public static Mediator Init(VisualEngine ve, ){
//
//    }

    private static final Hashtable<Class, EventHandler> handlers = new Hashtable<>();

    static {
        handlers.put(FlowerSunSpawnEvent.class, new FlowerSunSpawnEventHandler());
        handlers.put(SkySunSpawnEvent.class, new SkySunSpawnEventHandler());
        handlers.put(MapSpawnEvent.class, new MapSpawnEventHandler());
        handlers.put(PlantSpawnEvent.class, new PlantSpawnEventHandler());
    }

    public static void _notify(AbstractEvent event) {
        try {
            Platform.runLater(() -> {
                try {
                    handlers.get(event.getClass()).handle(event);

                } catch (Exception ex) {
                    System.out.println("Exception occurred on event handle: " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
        }
    }
}
