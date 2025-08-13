package com.pvz.plantsvszombies.Mediator;

import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;

import java.time.Duration;

public class Mediator {

    private final GameEngine _gameEngine;
    private final VisualEngine _visualEngine;

    private boolean _engineThreadRunning = false;

    private static Mediator _instance;

    public static void init(GameEngine gameEngine, VisualEngine visualEngine) {
        _instance = new Mediator(gameEngine, visualEngine);
    }

    public static Mediator getInstance() {
        return _instance;
    }

    public Mediator(GameEngine gameEngine, VisualEngine visualEngine) {
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

    public VisualEngine getVisualEngine() {
        return _visualEngine;
    }

    private void stopVisualEngine() {
        _visualEngine.stopEngine();
    }

    public void loadGame(){

    }
}
