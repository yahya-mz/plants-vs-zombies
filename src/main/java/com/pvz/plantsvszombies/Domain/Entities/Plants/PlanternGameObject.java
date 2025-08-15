package com.pvz.plantsvszombies.Domain.Entities.Plants;

import com.pvz.plantsvszombies.Domain.Common.Coordinate;
import com.pvz.plantsvszombies.Domain.Entities.FogGameObject;
import com.pvz.plantsvszombies.Domain.Interfaces.GameEngine;
import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;

import java.io.Serializable;
import java.util.ArrayList;

public class PlanternGameObject extends AbstractPlantGameObject implements Serializable {

    private transient final ArrayList<IEventSubscriber> _lightOnSubscribers  = new ArrayList<>();
    private transient final ArrayList<IEventSubscriber> _lightOffSubscribers = new ArrayList<>();
    private final int radius = 1; // 3x3

    public static PlanternGameObject createPlanternGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        return new PlanternGameObject(gameEngine, id, coordinate, row, column);
    }

    private PlanternGameObject(GameEngine gameEngine, String id, Coordinate coordinate, int row, int column) {
        this._gameEngine = gameEngine;
        this._ID = id;
        this._coordinate = coordinate;
        this._row = row;
        this._column = column;

        this._cost = 25;   // هرچی خواستی
        this._health = 100;
    }

    public void subscribeLightUpEvent(IEventSubscriber sub)  { _lightOnSubscribers.add(sub);  }
    public void subscribeLightOff(IEventSubscriber sub) { _lightOffSubscribers.add(sub); }

    @Override
    public void spawn() {
        // روشن‌کردن مه 3x3 اطراف
        lightUpArea();
        // به ویژوال بگو انیمیشن روشن‌بودن رو نمایش بده
        _lightOnSubscribers.forEach(s -> s._notify(this));
    }

    @Override
    public void update() {
        // معمولا لازم نیست کاری انجام بده؛
        // اگر می‌خوای fail-safe باشه که همیشه مه روشن بمونه:
        // lightUpArea();
    }

    @Override
    public void getHit(int damage) {
        _health -= damage;
        if (_health <= 0) {
            dispose();
        }
    }

    private void lightUpArea() {
        var fogs = _gameEngine.queryFogs(f ->
                Math.abs(f.getRow() - _row) <= radius &&
                        Math.abs(f.getColumn() - _column) <= radius
        );

        for (FogGameObject fog : fogs) {
            fog.fadeAwayCompletely();
        }
    }



    @Override
    public void dispose() {
        super.dispose();
    }


//    private void darkenArea() {
//        if (!(_gameEngine instanceof NightEngine ne)) return;
//
//        List<FogGameObject> fogs = ne.queryFogs(f ->
//                Math.abs(f.getRow() - _row) <= radius &&
//                        Math.abs(f.getColumn() - _column) <= radius
//        );
//
//        for (FogGameObject fog : fogs) {
//            fog.hideBySource(_ID);    // وقتی Plantern مُرد/رفت، اثرش از روی مه برداشته شود
//        }
//    }
}
