package com.pvz.plantsvszombies.Domain.Entities.Plants;
import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.GraveGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

public class GraveBusterGameObject extends AbstractPlantGameObject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public final static Duration EAT_TIME = Duration.ofSeconds(3);

    private int _tick;
    private transient final ArrayList<IEventSubscriber> _eatEventSubscribers = new ArrayList<>();

    public static GraveBusterGameObject createGraveBusterGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new GraveBusterGameObject(gameEngine, id, coordinate, row, column);
    }

    private GraveBusterGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 75;
        this._health = 50;
    }

    public void subscribeToEatEvent(IEventSubscriber event) {
        this._eatEventSubscribers.add(event);
    }

    @Override
    public void spawn() {
        // وقتی اسپاون شد، فقط صبر می‌کنیم تا زمان خوردن قبر برسه
    }

    @Override
    public void update() {
        _tick++;
        if (getMilliseconds() >= EAT_TIME.toMillis()) {
            eatGrave();
        }
    }

    private void eatGrave() {
        // حذف قبر از منطق بازی
        var block = _gameEngine.getBlock(_row, _column);
        GraveGameObject grave = block.getGrave();
        if (grave != null) {
            grave.dispose(true);
        }

        // ایونت به Visual بفرست
        for (IEventSubscriber sub : _eatEventSubscribers) {
            sub._notify(this);
        }

        // خودشو حذف کن
        super.dispose();
    }

    private double getMilliseconds() {
        return this._tick * 1000.0 / GlobalSettings.FPS;
    }

    @Override
    public void getHit(int damage) {
        // GraveBuster آسیب‌پذیر نیست
    }
}
