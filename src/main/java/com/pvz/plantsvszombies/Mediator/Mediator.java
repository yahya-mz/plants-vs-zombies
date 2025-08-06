package com.pvz.plantsvszombies.Mediator;

import com.pvz.plantsvszombies.Domain.Entities.Events.*;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import com.pvz.plantsvszombies.Presentation.EventHandlers.*;
import javafx.application.Platform;

import java.time.Duration;
import java.util.Hashtable;

public class Mediator {

//    public static Mediator Init(VisualEngine ve, ){
//
//    }

    private GameEngine _gameEngine;
    private IVisualEngine _visualEngine;

    private boolean _engineThreadRunning = false;

    private static final Hashtable<Class, EventHandler> handlers = new Hashtable<>();

    private static Mediator _instance;

    public static void init(GameEngine gameEngine, IVisualEngine visualEngine) {
        _instance = new Mediator(gameEngine, visualEngine);
    }

    public static Mediator getInstance() {
        return _instance;
    }

    public Mediator(GameEngine gameEngine, IVisualEngine visualEngine) {
        this._gameEngine = gameEngine;
        this._visualEngine = visualEngine;
    }

    public void startGameEngine() {
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

    public void stopGameEngine() {
        _engineThreadRunning = false;
        stopVisualEngine();
    }

    public IVisualEngine getVisualEngine() {
        return _visualEngine;
    }

    private void stopVisualEngine() {
        _visualEngine.stopEngine();
    }

}
