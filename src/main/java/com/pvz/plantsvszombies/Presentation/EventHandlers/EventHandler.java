package com.pvz.plantsvszombies.Presentation.EventHandlers;

import com.pvz.plantsvszombies.Domain.Entities.Events.AbstractEvent;

public interface EventHandler<T extends AbstractEvent> {
    void handle(T event) throws Exception;
}
