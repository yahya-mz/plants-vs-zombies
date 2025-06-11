package com.pvz.plantsvszombies.Mediator;

import com.pvz.plantsvszombies.Domain.Entities.Events.AbstractEvent;
import com.pvz.plantsvszombies.Domain.Entities.Events.FlowerSunSpawnEvent;
import com.pvz.plantsvszombies.Domain.Entities.Events.SkySunSpawnEvent;
import com.pvz.plantsvszombies.Presentation.EventHandlers.EventHandler;
import com.pvz.plantsvszombies.Presentation.EventHandlers.FlowerSunSpawnEventHandler;
import com.pvz.plantsvszombies.Presentation.EventHandlers.SkySunSpawnEventHandler;
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
    }

    public static void _notify(AbstractEvent event) {
        try {
            Platform.runLater(() -> {
                try {
                    handlers.get(event.getClass()).handle(event);

                } catch (Exception ex) {

                }
            });
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
        }
    }
}
