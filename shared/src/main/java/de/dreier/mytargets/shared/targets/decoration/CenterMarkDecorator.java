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

package de.dreier.mytargets.shared.targets.decoration;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CenterMarkDecorator implements TargetDecorator {
    public final int color;
    public final float size;
    public final int stroke;
    private final boolean tilted;
    private Paint paintStroke;

    public CenterMarkDecorator(int color, float size, int stroke, boolean tilted) {
        this.color = color;
        this.size = size;
        this.stroke = stroke;
        this.tilted = tilted;
    }

    private void initPaint() {
        paintStroke = new Paint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAntiAlias(true);
    }

    @Override
    public void drawDecoration(Canvas canvas) {
        if (paintStroke == null) {
            initPaint();
        }
        paintStroke.setColor(color);
        paintStroke.setStrokeWidth(stroke / 500f);
        if (tilted) {
            canvas.drawLine(-size * 0.002f, -size * 0.002f,
                    size * 0.002f, size * 0.002f, paintStroke);
            canvas.drawLine(-size * 0.002f, size * 0.002f,
                    size * 0.002f, -size * 0.002f, paintStroke);
        } else {
            canvas.drawLine(-size * 0.002f, 0, size * 0.002f, 0, paintStroke);
            canvas.drawLine(0, -size * 0.002f, 0, size * 0.002f, paintStroke);
        }
    }
}
