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
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

public class Circle {
    private final float density;
    private final Target target;
    @NonNull
    private final Paint circleColorPaint;
    @NonNull
    private final Paint textPaint;

    public Circle(float density, Target target) {
        this.density = density;
        this.target = target;

        // Set up default Paint object
        circleColorPaint = new Paint();
        circleColorPaint.setAntiAlias(true);

        // Set up a default TextPaint object
        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(@NonNull Canvas can, float x, float y, int zone, int radius, int arrow, @Nullable String number, boolean ambientMode) {
        ZoneBase zoneBase = target.getModel().getZone(zone);
        int fillColor = ambientMode ? Color.BLACK : zoneBase.getFillColor();
        int borderColor = ambientMode ? Color.WHITE : Color.getStrokeColor(zoneBase.getFillColor());
        int textColor = ambientMode ? Color.WHITE : zoneBase.getTextColor();
        String score = target.zoneToString(zone, arrow);
        drawScore(can, x, y,
                radius * density, score,
                ambientMode ? null : number, fillColor, borderColor, textColor);
    }

    public void drawScore(Canvas canvas, float x, float y, float radius, String score, String arrowNumber, int fillColor, int borderColor, int textColor) {
        int fontSize = (int) (1.2323f * radius + 0.7953f);

        // Draw the circles background
        circleColorPaint.setStrokeWidth(2);
        circleColorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circleColorPaint.setColor(fillColor);
        canvas.drawCircle(x, y, radius, circleColorPaint);

        // Draw the circles border
        circleColorPaint.setStyle(Paint.Style.STROKE);
        circleColorPaint.setColor(borderColor);
        canvas.drawCircle(x, y, radius, circleColorPaint);

        // Draw the text inside the circle
        textPaint.setColor(textColor);
        textPaint.setTextSize(fontSize);
        canvas.drawText(score, x, y + fontSize * 7 / 22.0f, textPaint);

        if (arrowNumber != null) {
            circleColorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            circleColorPaint.setColor(0xFF333333);
            canvas.drawCircle(x + radius * 0.8f, y + radius * 0.8f, radius * 0.5f, circleColorPaint);
            textPaint.setTextSize(fontSize * 0.5f);
            textPaint.setColor(0xFFFFFFFF);
            canvas.drawText(arrowNumber, x + radius * 0.8f, y + radius * 1.05f, textPaint);
        }
    }
}
