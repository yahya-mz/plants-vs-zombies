package com.pvz.plantsvszombies.Domain.Entities.Events;

import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;
import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.Mediator.Mediator;

public class MapSpawnEvent extends AbstractEvent {
    public static void emit(MapGameObject object) {
        var event = new MapSpawnEvent(object);
//        Mediator._notify(event);
    }

    MapSpawnEvent(MapGameObject object) {
        this._gameObject = object;
    }
}
