package com.pvz.plantsvszombies.Domain.Entities;

import com.pvz.plantsvszombies.Domain.Interfaces.IEventSubscriber;
import com.pvz.plantsvszombies.GlobalSettings;

import java.time.Duration;
import java.util.ArrayList;

public class FogGameObject extends AbstractGameObject {

    public static Duration FADING_COOL_DOWN = Duration.ofSeconds(10);

    private int _tick;
    private int _column;

    private boolean _isFadingAway = false;
    private boolean _isFadedAway = false;
    private double _speed;
    private int _lastFadingTick = 0;

    private final ArrayList<MapBlock> _foggedBlocks = new ArrayList<>();


    public final ArrayList<IEventSubscriber> _movementEventSubscribers = new ArrayList<>();

    public int getColumn() {
        return _column;
    }

    public void subscribeToMovementEvent(IEventSubscriber eventSubscriber) {
        _movementEventSubscribers.add(eventSubscriber);
    }

    public void fadeAwayCompletely() {
        if (!_isFadedAway) {
            _isFadingAway = true;
            _lastFadingTick = _tick;
        }
    }

    @Override
    public void spawn() {

    }

    @Override
    public void update() {
        if (_isFadedAway &&
                ((_tick - _lastFadingTick) * 1000.0 / GlobalSettings.FPS) > FADING_COOL_DOWN.toMillis()) {
            _coordinate.traverse(-(_speed), 0);
            if (_coordinate.y() == _gameEngine.getWindowWidth() / 2) {
                _isFadedAway = false;
            }
        }
        if (_isFadingAway) {
            fadeABit();
            if (_coordinate.y() == _gameEngine.getWindowWidth()) {
                _isFadingAway = false;
                _isFadedAway = true;
                _lastFadingTick = 0;
            }
        }

    }

    private void fadeABit() {
        _coordinate.traverse(-10, 0);
        _movementEventSubscribers.forEach(e -> {
            e._notify(this);
        });
    }
}
