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
import android.text.TextPaint;

import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

public class Circle {
    private final float density;
    private final Target target;
    private final Paint circleColorP;
    private final Paint textPaint;

    public Circle(float density, Target target) {
        this.density = density;
        this.target = target;

        // Set up default Paint object
        circleColorP = new Paint();
        circleColorP.setAntiAlias(true);

        // Set up a default TextPaint object
        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(Canvas can, float x, float y, int zone, int rad, int arrow, String number, boolean ambientMode) {
        ZoneBase zoneBase = target.getModel().getZone(zone);

        // Draw the circles background
        circleColorP.setStrokeWidth(2 * density);
        circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
        circleColorP.setColor(ambientMode ? Color.BLACK : zoneBase.getFillColor());
        can.drawCircle(x, y, rad * density, circleColorP);

        // Draw the circles border
        circleColorP.setStyle(Paint.Style.STROKE);
        circleColorP.setColor(
                ambientMode ? Color.WHITE : Color.getStrokeColor(zoneBase.getFillColor()));
        can.drawCircle(x, y, rad * density, circleColorP);

        // Draw the text inside the circle
        textPaint.setColor(ambientMode ? Color.WHITE : zoneBase.getTextColor());
        int font_size = (int) (1.2323f * rad + 0.7953f);
        textPaint.setTextSize(font_size * density);
        can.drawText(target.zoneToString(zone, arrow), x, y + font_size * 7 * density / 22.0f,
                textPaint);

        if (!ambientMode && number != null) {
            circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
            circleColorP.setColor(0xFF333333);
            can.drawCircle(x + rad * 0.8f * density, y + rad * 0.8f * density, 8 * density, circleColorP);
            textPaint.setTextSize(font_size * density * 0.5f);
            textPaint.setColor(0xFFFFFFFF);
            can.drawText(number, x + rad * 0.8f * density, y + rad * 1.05f * density, textPaint);
        }
    }
}
