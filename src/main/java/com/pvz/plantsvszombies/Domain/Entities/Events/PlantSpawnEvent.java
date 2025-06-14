package com.pvz.plantsvszombies.Domain.Entities.Events;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.Mediator.Mediator;

public class PlantSpawnEvent extends AbstractEvent {
    public static void emit(AbstractPlantGameObject object) {
        var event = new PlantSpawnEvent(object);
//        Mediator._notify(event);
    }

    PlantSpawnEvent(AbstractPlantGameObject object) {
        this._gameObject = object;
    }
}
