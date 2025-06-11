package com.pvz.plantsvszombies.Domain.Entities.Events;

import com.pvz.plantsvszombies.Domain.Entities.SunGameObject;
import com.pvz.plantsvszombies.Mediator.Mediator;

public class SkySunSpawnEvent extends AbstractEvent{
    public static void emit(SunGameObject object) {
        var event = new SkySunSpawnEvent(object);
        Mediator._notify(event);
    }

    SkySunSpawnEvent(SunGameObject object){
        this._gameObject = object;
    }
}
