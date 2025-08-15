package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.GraveGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class GraveVisualObject extends AbstractVisualObject {

    private final IVisualEngine _engine;

    public GraveVisualObject(GraveGameObject gameObject, IVisualEngine engine) {
        super._gameObject = gameObject;
        this._engine = engine;

        // مختصات اولیه را از گیم‌اُبجکت می‌گیریم
        _visualCoordinate = gameObject.getCoordinate();

        // تصویر قبر (مسیر را با پروژه‌ات هماهنگ کن)
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Screen/Grave/Grave_0.png")));
        _node.setManaged(false);

        // تعامل‌ناپذیر (لازم نیست کلیک بخوره)
        _node.setMouseTransparent(true);
        _node.setPickOnBounds(false);
        _node.setFocusTraversable(false);

        // قرار دادن در مرکز بلوک
        var imgView = (ImageView) _node;
        var height = imgView.getImage().getHeight();
        var width  = imgView.getImage().getWidth();

        _node.relocate(_visualCoordinate.x() - 0.5 * width,
                _visualCoordinate.y() - 0.5 * height);

        // اگر آبجکت دیسپوز شد، ویژوال هم پاک شود
        ((GraveGameObject) _gameObject).subscribeToDisposeEvent(new IEventSubscriber() {
            @Override
            public void _notify(AbstractGameObject gameObject) {
                Platform.runLater(() -> _engine.disposeObject(GraveVisualObject.this));
            }
        });
    }

    @Override
    public void spawn() {
        // الان فقط ظاهر میشه. اگر خواستی می‌تونیم یه fade-in ساده هم اضافه کنیم.
        _gameObject.spawn();
        // مثال (در صورت داشتن GeneralFadingAnimation):
        // GeneralFadingAnimation.attach(this).fromOpacity(0).toOpacity(1, Duration.millis(150));
    }
}
