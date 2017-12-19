/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.training.input;

import android.graphics.PointF;
import android.support.annotation.Nullable;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Detects finger slipping while releasing the finger from the screen.
 * This simply ignores the positions recorded during the last 100ms.
 */
class FingerSlipDetector {
    private static final long TIME_WINDOW = 100; // in milliseconds
    private static final int INITIAL_CACHE_SIZE = 20;

    private Stack<TimedPoint> cache;
    private LinkedList<TimedPoint> list;
    @Nullable
    private TimedPoint currentMaturePosition = null;

    public FingerSlipDetector() {
        list = new LinkedList<>();
        cache = new Stack<>();
        for (int i = 0; i < INITIAL_CACHE_SIZE; i++) {
            cache.push(new TimedPoint());
        }
    }

    public void addShot(float x, float y) {
        if (cache.isEmpty()) {
            cache.push(new TimedPoint());
        }
        TimedPoint point = cache.pop();
        point.time = System.currentTimeMillis();
        point.x = x;
        point.y = y;
        if (currentMaturePosition == null) {
            currentMaturePosition = point;
        } else {
            list.add(point);
        }
        while (!list.isEmpty() && list.getFirst().time < point.time - TIME_WINDOW) {
            cache.push(currentMaturePosition);
            currentMaturePosition = list.removeFirst();
        }
    }

    @Nullable
    public PointF getFinalPosition() {
        if (currentMaturePosition == null) {
            return null;
        }

        return new PointF(currentMaturePosition.x, currentMaturePosition.y);
    }

    public void reset() {
        if (currentMaturePosition == null) {
            return;
        }

        cache.push(currentMaturePosition);
        currentMaturePosition = null;
        cache.addAll(list);
        list.clear();
    }

    private class TimedPoint {
        Long time;
        float x;
        float y;
    }
}
