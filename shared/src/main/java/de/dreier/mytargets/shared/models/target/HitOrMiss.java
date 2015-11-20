/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class HitOrMiss extends CircularTargetBase {

    public HitOrMiss(Context context) {
        super(context, 14, R.string.hit_or_miss);
        zones = 2;
        radius = new float[]{125,500};
        colorFill = new int[]{YELLOW, RED_MISS};
        colorStroke = new int[]{STROKE_GRAY, STROKE_GRAY};
        strokeWidth = new int[] {3,3};
        zonePoints = new int[][]{{1,0}};
        showAsX = new boolean[]{false};
        diameters = new Diameter[]{new Diameter(30, Dimension.CENTIMETER),
                new Diameter(96, Dimension.CENTIMETER)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(STROKE_GRAY);
        final float size = recalc(rect, 4.188f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY(),
                rect.exactCenterX() + size, rect.exactCenterY(), paintStroke);
        canvas.drawLine(rect.exactCenterX(), rect.exactCenterY() - size,
                rect.exactCenterX(), rect.exactCenterY() + size, paintStroke);
    }
}
