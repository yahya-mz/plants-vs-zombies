package com.pvz.plantsvszombies.Presentation.GUI.Views;

import com.pvz.plantsvszombies.Domain.Entities.Plants.AbstractPlantGameObject;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundManager;
import com.pvz.plantsvszombies.GlobalMusicSettings.SoundType;
import com.pvz.plantsvszombies.GlobalSettings;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public class EffectsManagement {
    private static ImageCursor pointerCustomCursor;
    private static ImageCursor normalCustomCursor;

    public static void yAndScaleHoverEffectForNode(Node node) {
        Image pointerCursorImage = new Image(GlobalSettings.getResource("graphics/Items/Cursor/DayPointerCursor.png"));
        pointerCustomCursor = new ImageCursor(pointerCursorImage, pointerCursorImage.getWidth() / 2, pointerCursorImage.getHeight() / 2);
        Image normalcursorImage = new Image(GlobalSettings.getResource("graphics/Items/Cursor/DayCursor.png"));
        normalCustomCursor = new ImageCursor(normalcursorImage, normalcursorImage.getWidth() / 2, normalcursorImage.getHeight() / 2);

//        final BooleanProperty hoverArmed = new SimpleBooleanProperty(true);
//        node.setPickOnBounds(true);
        node.setOnMouseEntered( ev -> {
            animateNode(node, -14, 1.1, 100);
            SoundManager.play(SoundType.HOVER_CLICK);
            node.setCursor(pointerCustomCursor);
        });
        node.setOnMousePressed(e -> {
                SoundManager.play(SoundType.SELECTED_CLICK);
        });
        node.setOnMouseExited(ev  -> {
            animateNode(node,   0, 1.0, 120);
            node.getScene().setCursor(normalCustomCursor);
        });
    }
    public static void scaleHoverEffectForNode(Node node) {
        node.setOnMouseEntered(ev -> {
            animateNode(node, 0, 1.1, 100);
            SoundManager.play(SoundType.HOVER_CLICK);
            node.getScene().setCursor(pointerCustomCursor);
        });
        node.setOnMousePressed(e -> {
            SoundManager.play(SoundType.SELECTED_CLICK);
        });
        node.setOnMouseExited(ev  -> animateNode(node,   0, 1.0, 120));
    }
    private static void animateNode(Node node, double toY, double scale, int ms) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ms), node);
        tt.setToY(toY);
        tt.playFromStart();

        ScaleTransition st = new ScaleTransition(Duration.millis(ms), node);
        st.setToX(scale);
        st.setToY(scale);
        st.playFromStart();
    }


    public static void applyDeactivateEffect(Button btn) {
        ColorAdjust gray = new ColorAdjust();
        gray.setSaturation(-1.0);   // خاکستری کردن
        gray.setBrightness(-0.45);  // کمی تیره‌تر
        btn.setEffect(gray);
        btn.setDisable(true);
    }

    public static Duration getCooldownFor(Button btn) {
        Object ud = btn.getUserData();
        if (!(ud instanceof AbstractPlantGameObject.PlantType type)) {
            return Duration.seconds(5);
        }

        switch (type) {
            case PEASHOOTER:    return Duration.seconds(5);
            case SUNFLOWER:     return Duration.seconds(20);
            case WALL_NUT:      return Duration.seconds(12);
            case JALAPENO:      return Duration.seconds(10);
            case TALL_NUT:      return Duration.seconds(15);
            case CHERRY_BOMB:   return Duration.seconds(15);
            case SNOW_PEA:      return Duration.seconds(8);
            case REPEATER:      return Duration.seconds(7);
            default:            return Duration.seconds(5);
        }
    }
//
//    public static void adjustNodeWithMargins(Node node, double sceneWidth ) {
//        double rightMargin = 80;
//        double leftMargin = 80;
//        if (!(node instanceof ImageView iv)) {
//            return;
//        }
//
//        // جلوگیری از بزرگتر شدن margin از نصف عرض صحنه
//        double maxLeft  = Math.max(0, Math.min(leftMargin,  sceneWidth / 2.0));
//        double maxRight = Math.max(0, Math.min(rightMargin, sceneWidth / 2.0));
//
//        // محاسبه عرض جدید
//        double targetWidth = Math.max(1, sceneWidth - (maxLeft + maxRight));
//
//        iv.setPreserveRatio(false);
//        iv.setFitWidth(targetWidth);
//
//        // اینجا ارتفاع رو هم کش می‌دهیم تا تغییر سایز واقعی بشه
//        iv.setFitHeight(iv.getImage().getHeight() * (targetWidth / iv.getImage().getWidth()));
//
//        // تغییر موقعیت
//        if (node.getParent() instanceof Pane) {
//            node.setLayoutX(maxLeft);
//        } else {
//            var bounds = node.localToScene(node.getBoundsInLocal());
//            double dx = maxLeft - bounds.getMinX();
//            node.setTranslateX(node.getTranslateX() + dx);
//        }
//    }


    public static ImageCursor getPointerCustomCursor() {
        return pointerCustomCursor;
    }

    public static ImageCursor getNormalCustomCursor() {
        return normalCustomCursor;
    }
}
