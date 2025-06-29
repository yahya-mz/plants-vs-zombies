package com.pvz.plantsvszombies.Mediator;

import com.pvz.plantsvszombies.Domain.Entities.Events.*;
import com.pvz.plantsvszombies.Domain.Entities.IGameEngine;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.EventHandlers.*;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import javafx.application.Platform;

import javax.print.attribute.standard.Media;
import java.time.Duration;
import java.util.Hashtable;

public class Mediator {

//    public static Mediator Init(VisualEngine ve, ){
//
//    }

    private IGameEngine _gameEngine;
    private VisualEngine _visualEngine;

    private boolean _engineThreadRunning = false;

    private static final Hashtable<Class, EventHandler> handlers = new Hashtable<>();

    private static Mediator _instance;

    public static void init(IGameEngine gameEngine, VisualEngine visualEngine) {
        _instance = new Mediator(gameEngine, visualEngine);
    }

    public static Mediator getInstance() {
        return _instance;
    }

    public Mediator(IGameEngine gameEngine, VisualEngine visualEngine) {
        this._gameEngine = gameEngine;
        this._visualEngine = visualEngine;
    }

    private Mediator() {
//        handlers.put(FlowerSunSpawnEvent.class, new FlowerSunSpawnEventHandler());
//        handlers.put(SkySunSpawnEvent.class, new SkySunSpawnEventHandler());
//        handlers.put(MapSpawnEvent.class, new MapSpawnEventHandler());
//        handlers.put(PlantSpawnEvent.class, new PlantSpawnEventHandler());
    }

    public void _notify(AbstractEvent event) {
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

    public void startEngine() {
        _engineThreadRunning = true;
        var gameEngineThread = new Thread(() -> {
            try {
                _gameEngine.start();
                while (_engineThreadRunning) {
                    try {
                        _gameEngine.update();
                        Thread.sleep(Duration.ofMillis(1000 / GlobalSettings.FPS));
                    } catch (Exception ex) {
                        System.out.println("Exception in MainApp");
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                System.out.println("Exception in GameEngine Thread: " + ex.getMessage());
            }
        });
        gameEngineThread.start();
    }

    public void stopEngine() {
        _engineThreadRunning = false;
    }

}
