package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.FogGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FogVisualObject extends AbstractVisualObject {

    private final VisualEngine _engine;

    public FogVisualObject(FogGameObject gameObject, VisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Screen/Fog/Fog_0.png")));
        _node.setManaged(false);

        var height = ((Image) ((ImageView) _node).getImage()).getHeight();
        var width = ((Image) ((ImageView) _node).getImage()).getWidth();

        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - 0.5 * height);

        ((FogGameObject)_gameObject).subscribeToMovementEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                _visualCoordinate = gameObject.getCoordinate();
                _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - 0.5 * height);

            }
        });
    }

    @Override
    public void spawn() {

    }
}
