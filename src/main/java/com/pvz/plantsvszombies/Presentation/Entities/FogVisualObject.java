package com.pvz.plantsvszombies.Presentation.Entities;

import com.pvz.plantsvszombies.Domain.Entities.AbstractGameObject;
import com.pvz.plantsvszombies.Domain.Entities.FogGameObject;
import com.pvz.plantsvszombies.Domain.Entities.FogGameObject;
import com.pvz.plantsvszombies.Domain.Entities.Plants.BloverGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;
import com.pvz.plantsvszombies.Presentation.Animations.GeneralFadingAnimation;
import com.pvz.plantsvszombies.Presentation.Engines.IVisualEngine;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;



public class FogVisualObject extends AbstractVisualObject {

    private final IVisualEngine _engine;
    private GeneralFadingAnimation _fadingAnimation;

    private double homeX;
    private double maxDistance = 1;       // برای نرمال‌سازی
    private double lastDistance = 0;  // برای تقسیم، از صفر پرهیز کنیم
    private boolean returning = false;
    private boolean fadeOutStarted = false;
    private static final double returnThreshold = 6.0;   // آستانه رسیدن به خانه
    private static final double detectEpsilon    = 0.5;  // برای تشخیص کاهش فاصله
    private static final double minOpacity       = 0.10; // مهِ کمینه وقتی خیلی دور شده
    private static final Duration fadeOutDur     = Duration.millis(1000);

    public FogVisualObject(FogGameObject gameObject, IVisualEngine engine) {
        super._gameObject = gameObject;
        _engine = engine;

        _visualCoordinate = gameObject.getCoordinate();
        _node = new ImageView(new Image(GlobalSettings.getResource("graphics/Screen/Fog/Fog_0.png")));
        _node.setManaged(false);

        var img = (ImageView) _node;
        var height = img.getImage().getHeight();
        var width  = img.getImage().getWidth();

        _node.relocate(_visualCoordinate.x() - 0.5 * width, _visualCoordinate.y() - 0.5 * height);

        homeX = _visualCoordinate.x();
        _fadingAnimation = GeneralFadingAnimation.attach(this); // ← مهم!
        img.setOpacity(1.0);
        lastDistance = Math.abs(_visualCoordinate.x() - homeX); // 0

        ((FogGameObject) _gameObject).subscribeToMovementEvent(gameObj -> {
            _visualCoordinate = gameObj.getCoordinate();

            double x = _visualCoordinate.x();
            double distance = Math.abs(x - homeX);

            // به‌روزرسانی جای تصویر
            Platform.runLater(() -> _node.relocate(
                    _visualCoordinate.x() - 0.5 * width,
                    _visualCoordinate.y() - 0.5 * height
            ));

            // رکورد بیشترین فاصله
            if (distance > maxDistance) maxDistance = distance;

            // 1) شروع رفتن: یکبار fadeOut قدیمی
            if (!fadeOutStarted && distance > returnThreshold) {
                fadeOutStarted = true;
                returning = false;
                Platform.runLater(() -> {
                    try { _fadingAnimation.interrupt(); } catch (Exception ignored) {}
                    // تا minOpacity محو می‌شویم؛ اگر دوست داری تا 0 بروی، 0 بگذار
                    _fadingAnimation.toOpacity(minOpacity, fadeOutDur);
                });
            }

            // 2) تشخیص شروع برگشت: وقتی فاصله شروع کند کاهش یابد
            if (fadeOutStarted && !returning && (lastDistance - distance) > detectEpsilon) {
                returning = true;
                Platform.runLater(() -> {
                    try { _fadingAnimation.interrupt(); } catch (Exception ignored) {}
                });
            }

            // 3) در حال برگشت: روشن شدن تدریجی بر اساس فاصله
            if (fadeOutStarted && returning) {
                double span = Math.max(1e-6, maxDistance - returnThreshold);
                double progressBack = (maxDistance - distance) / span;   // 0..1
                progressBack = Math.max(0.0, Math.min(1.0, progressBack));
                double targetOpacity = minOpacity + gameObject.getBackProgress() * (1.0 - minOpacity);


                Platform.runLater(() -> ((ImageView) _node).setOpacity(targetOpacity));
            }

            // 4) وقتی رسید: ریست حالت‌ها و اوپاسیتیِ کامل
            if (fadeOutStarted && distance <= returnThreshold) {
                fadeOutStarted = false;
                returning = false;
                maxDistance = Math.max(1, maxDistance * 0.5); // نرم‌تر برای دور بعدی
                Platform.runLater(() -> ((ImageView) _node).setOpacity(1.0));
            }

            lastDistance = distance;
        });
    }

    @Override
    public void spawn() { }
}

