package com.pvz.plantsvszombies.Domain.Entities.Events;

import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.Mediator.Mediator;

public class FlowerSunSpawnEvent extends AbstractEvent {
    public static void emit(SunGameObject object) {
        var event = new FlowerSunSpawnEvent(object);
//        Mediator._notify(event);
    }

    FlowerSunSpawnEvent(SunGameObject object){
        this._gameObject = object;
    }
}
