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
import de.dreier.mytargets.shared.models.Dimension;

public class WA6RingTarget extends CircularTargetBase {

    public WA6RingTarget(Context context) {
        super(context, 1, R.string.wa_6_ring);
        zones = 7;
        radius = new int[]{42, 83, 167, 250, 333, 417, 500};
        colorFill = new int[]{LEMON_YELLOW, LEMON_YELLOW, LEMON_YELLOW, FLAMINGO_RED, FLAMINGO_RED,
                CERULEAN_BLUE, CERULEAN_BLUE};
        colorStroke = new int[]{Target.DARK_GRAY, Target.DARK_GRAY, Target.DARK_GRAY,
                Target.DARK_GRAY, Target.DARK_GRAY,
                Target.DARK_GRAY, Target.DARK_GRAY};
        strokeWidth = new int[] {3, 3, 3, 3, 3, 3, 3};
        zonePoints = new int[][]{{10, 10, 9, 8, 7, 6, 5},
                {10, 9, 9, 8, 7, 6, 5},
                {11, 10, 9, 8, 7, 6, 5},
                {5, 5, 5, 4, 4, 3, 3},
                {9, 9, 9, 7, 7, 5, 5}};
        showAsX = new boolean[]{true, false, false, true, false};
        diameters = new Dimension[]{new Dimension(40, Dimension.CENTIMETER),
                new Dimension(60, Dimension.CENTIMETER),
                new Dimension(80, Dimension.CENTIMETER),
                new Dimension(92, Dimension.CENTIMETER),
                new Dimension(122, Dimension.CENTIMETER)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(Target.DARK_GRAY);
        final float size = recalc(rect,8.333f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY(),
                rect.exactCenterX() + size, rect.exactCenterY(), paintStroke);
        canvas.drawLine(rect.exactCenterX(), rect.exactCenterY() - size,
                rect.exactCenterX(), rect.exactCenterY() + size, paintStroke);
    }
}
