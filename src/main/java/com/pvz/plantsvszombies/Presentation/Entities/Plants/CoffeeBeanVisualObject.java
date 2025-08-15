package com.pvz.plantsvszombies.Presentation.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.CoffeeBeanGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.CoffeeBeanAnimations;
import com.pvz.plantsvszombies.Presentation.Animations.IAnimation;
import com.pvz.plantsvszombies.Presentation.Engines.VisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class CoffeeBeanVisualObject extends AbstractPlantVisualObject {

    private final VisualEngine _engine;

    public CoffeeBeanVisualObject(CoffeeBeanGameObject gameObject, VisualEngine engine) {

        System.out.println("demoed");
        super._gameObject = gameObject;
        this._engine = engine;

        var temp_this = this;
        gameObject.subscribeToExplosionEvent(new IEventSubscriber() {//notify
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> {
                    playAnimation(CoffeeBeanAnimations.Animations.DYING, Duration.millis(50), 1);
                    setOnAnimationFinished(e -> {
                        _engine.disposeObject(temp_this);
                    });
                });
            }
        });

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Plants/CoffeeBean/CoffeeBean_0.png")));
        ((ImageView) _node).setFitWidth(85);
        ((ImageView) _node).setFitHeight(85);
    }

    @Override
    public void playAnimation(IAnimation animation, Duration frameDuration) {
        super.playAnimation(CoffeeBeanAnimations.getFrames((CoffeeBeanAnimations.Animations) animation), frameDuration);
    }

    public void playAnimation(IAnimation animation, Duration frameDuration, int cycleCount) {
        super.playAnimation(CoffeeBeanAnimations.getFrames((CoffeeBeanAnimations.Animations) animation), frameDuration, cycleCount);
    }

    @Override
    public void spawn() {
        Platform.runLater(() -> {
            _gameObject.spawn();
            var framesCount = CoffeeBeanAnimations.getFrames(CoffeeBeanAnimations.Animations.STANDING).length;
            playAnimation(CoffeeBeanAnimations.Animations.STANDING, Duration.millis(CoffeeBeanGameObject.EXPLOSION_TIME.toMillis()).divide(framesCount), 1);//standing//the animation is not inf is one time only
        });
    }

}
