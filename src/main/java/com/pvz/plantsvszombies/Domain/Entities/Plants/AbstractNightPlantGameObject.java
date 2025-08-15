package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;

import java.util.ArrayList;

public abstract class AbstractNightPlantGameObject extends AbstractPlantGameObject {

    protected transient ArrayList<IEventSubscriber> _wakeUpEventSubscribers = new ArrayList<>();

    public void subscribeToWakeUpEvent(IEventSubscriber event) {
        this._wakeUpEventSubscribers.add(event);
    }


    protected boolean _isAwake = false;

    public boolean isAwake() {
        return _isAwake;
    }

    public void wakeUp() {
        if (!this._isAwake) {
            this._isAwake = true;
            for (IEventSubscriber subscriber : _wakeUpEventSubscribers) {
                subscriber._notify(this);
            }
        }
    }
}
