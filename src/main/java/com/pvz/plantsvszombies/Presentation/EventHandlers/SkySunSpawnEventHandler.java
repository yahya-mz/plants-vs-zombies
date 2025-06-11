package com.pvz.plantsvszombies.Presentation.EventHandlers;

import com.pvz.plantsvszombies.Domain.Entities.Events.SkySunSpawnEvent;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.Presentation.VisualEngine;
import com.pvz.plantsvszombies.Presentation.Entities.SkySunVisualObject;

public class SkySunSpawnEventHandler implements EventHandler<SkySunSpawnEvent> {
    @Override
    public void handle(SkySunSpawnEvent event) throws Exception {
        var gameObject = (SunGameObject) event._gameObject;
        SkySunVisualObject object = new SkySunVisualObject(gameObject);
        VisualEngine.getInstance().getLevelStage().spawnVisualObject(object);
    }
}
