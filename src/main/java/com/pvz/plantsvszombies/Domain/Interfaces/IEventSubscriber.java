package com.pvz.plantsvszombies.Domain.Interfaces;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;

public interface IEventSubscriber {
    void _notify(AbstractGameObject gameObject);
}
