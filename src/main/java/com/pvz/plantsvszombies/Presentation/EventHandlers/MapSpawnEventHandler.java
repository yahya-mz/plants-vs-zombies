package com.pvz.plantsvszombies.Presentation.EventHandlers;

import com.pvz.plantsvszombies.Domain.Entities.Events.MapSpawnEvent;
import com.pvz.plantsvszombies.Domain.Entities.Events.SkySunSpawnEvent;
import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.Presentation.Entities.MapVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.SkySunVisualObject;
import com.pvz.plantsvszombies.Presentation.VisualEngine;

public class MapSpawnEventHandler implements EventHandler<MapSpawnEvent> {
    @Override
    public void handle(MapSpawnEvent event) throws Exception {

    }
}
