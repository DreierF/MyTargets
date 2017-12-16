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

package de.dreier.mytargets.shared.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CircleDrawable extends Drawable {

    private Circle circle;
    private float x;
    private float y;
    private int radius;
    private String score;
    private String arrowNumber;
    private int fillColor;
    private int textColor;

    public CircleDrawable(float density, String score, String arrowNumber, int fillColor, int textColor) {
        this.circle = new Circle(density, null);
        this.score = score;
        this.arrowNumber = arrowNumber;
        this.fillColor = fillColor;
        this.textColor = textColor;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        circle.drawScore(canvas, x, y, radius, score, arrowNumber, fillColor, Color.getStrokeColor(fillColor), textColor);
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        x = bounds.exactCenterX();
        y = bounds.exactCenterY();
        radius = (int) (Math.min(bounds.width(), bounds.height()) * 0.45f);
    }
}
