package com.pvz.plantsvszombies.Domain.Entities.Events;

import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;
import com.pvz.plantsvszombies.Mediator.Mediator;

public class PlantSpawnEvent extends AbstractEvent {
    public static void emit(MapGameObject object) {
        var event = new MapSpawnEvent(object);
        Mediator._notify(event);
    }

    PlantSpawnEvent(MapGameObject object) {
        this._gameObject = object;
    }
}
