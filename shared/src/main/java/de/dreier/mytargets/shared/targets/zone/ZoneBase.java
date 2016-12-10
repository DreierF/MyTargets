/*
 * Copyright (C) 2016 Florian Dreier
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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import de.dreier.mytargets.shared.utils.Color;

public abstract class ZoneBase {
    public final float radius;
    public final int fillColor;
    public final int strokeColor;
    public final float strokeWidth;
    protected final PointF midpoint;
    protected final boolean scoresAsOutsideIn;

    Paint paintFill;
    Paint paintStroke;

    public ZoneBase(float radius, PointF midpoint, int fillColor, int strokeColor, int strokeWidth, boolean scoresAsOutsideIn) {
        this.radius = radius;
        this.midpoint = midpoint;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth * 0.002f;
        this.scoresAsOutsideIn = scoresAsOutsideIn;
    }

    protected void initPaint() {
        if (paintFill != null) {
            return;
        }
        paintFill = new Paint();
        paintFill.setAntiAlias(true);
        paintFill.setColor(fillColor);
        paintStroke = new Paint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAntiAlias(true);
        paintStroke.setColor(strokeColor);
        paintStroke.setStrokeWidth(strokeWidth);
    }

    public abstract boolean isInZone(float ax, float ay, float arrowRadius);

    public int getFillColor() {
        return fillColor;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public int getTextColor() {
        return Color.getContrast(fillColor);
    }

    public abstract void drawFill(Canvas canvas);

    public abstract void drawStroke(Canvas canvas);
}
