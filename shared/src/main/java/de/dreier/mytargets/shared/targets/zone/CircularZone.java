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

package de.dreier.mytargets.shared.targets.zone;

import android.graphics.PointF;

import de.dreier.mytargets.shared.targets.drawable.CanvasWrapper;

public class CircularZone extends ZoneBase {

    public CircularZone(float radius, int fillColor, int strokeColor, int strokeWidth) {
        this(radius, fillColor, strokeColor, strokeWidth, true);
    }

    public CircularZone(float radius, int fillColor, int strokeColor, int strokeWidth, boolean scoresAsOutsideIn) {
        this(radius, 0f, 0f, fillColor, strokeColor, strokeWidth, scoresAsOutsideIn);
    }

    public CircularZone(float radius, float midpointX, float midpointY, int fillColor, int strokeColor, int strokeWidth) {
        this(radius, midpointX, midpointY, fillColor, strokeColor, strokeWidth, true);
    }

    public CircularZone(float radius, float midpointX, float midpointY, int fillColor, int strokeColor, int strokeWidth, boolean scoresAsOutsideIn) {
        super(radius, new PointF(midpointX, midpointY), fillColor, strokeColor, strokeWidth,
                scoresAsOutsideIn);
    }

    @Override
    public boolean isInZone(float ax, float ay, float arrowRadius) {
        float distance =
                (ax - midpoint.x) * (ax - midpoint.x) + (ay - midpoint.y) * (ay - midpoint.y);
        float adaptedRadius =
                radius + (scoresAsOutsideIn ? 1f : -1f) * (arrowRadius + strokeWidth / 2.0f);
        return adaptedRadius * adaptedRadius > distance;
    }

    @Override
    public void drawFill(CanvasWrapper canvas) {
        initPaint();
        canvas.drawCircle(midpoint.x, midpoint.y, radius, paintFill);
    }


    @Override
    public void drawStroke(CanvasWrapper canvas) {
        initPaint();
        canvas.drawCircle(midpoint.x, midpoint.y, radius, paintStroke);
    }

}
