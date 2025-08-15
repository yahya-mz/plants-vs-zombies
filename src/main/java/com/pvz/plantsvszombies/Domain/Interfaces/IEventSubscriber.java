package com.pvz.plantsvszombies.Domain.Interfaces;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Bullets.AbstractBulletGameObject;

public interface IEventSubscriber {
    void _notify(AbstractGameObject gameObject);
}
