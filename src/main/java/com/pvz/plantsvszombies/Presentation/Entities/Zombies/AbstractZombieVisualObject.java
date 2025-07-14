package com.pvz.plantsvszombies.Presentation.Entities.Zombies;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Zombies.AbstractZombieGameObject;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractAnimatedVisualObject;

public abstract class AbstractZombieVisualObject extends AbstractAnimatedVisualObject {

    protected AbstractZombieGameObject _gameObject;

    public abstract AbstractZombieGameObject getGameObject();
}

