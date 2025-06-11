package com.pvz.plantsvszombies.Domain.Entities;

public interface IEventSubscriber {
    void _notify(IGameObject gameObject);
}
