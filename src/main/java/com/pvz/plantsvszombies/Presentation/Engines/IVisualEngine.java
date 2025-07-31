package com.pvz.plantsvszombies.Presentation.Engines;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Presentation.GUI.Views.AbstractLevelView;
import com.pvz.plantsvszombies.Presentation.Entities.AbstractVisualObject;
import com.pvz.plantsvszombies.Presentation.Entities.Plants.AbstractPlantVisualObject;

public interface IVisualEngine {
    int getWidth();

    int getHeight();

    void disposeObject(AbstractVisualObject obj);

    void stopEngine();

    void spawnVisualObject(AbstractVisualObject object);

    void spawnVisualObject(AbstractVisualObject object, int top_z_index);

    void plant(Class<? extends AbstractPlantVisualObject> plantType, int x, int y, Coordinate coordinate);

    void setSelectedPlantType(Class<? extends AbstractPlantVisualObject> plantType);

    Class<? extends AbstractPlantVisualObject> getSelectedPlantType();

    void clearSelectedPlantType();

    AbstractLevelView getLevelStage();

    void shovelRemover(int row, int col);

    abstract boolean isShovelActivated();
}
