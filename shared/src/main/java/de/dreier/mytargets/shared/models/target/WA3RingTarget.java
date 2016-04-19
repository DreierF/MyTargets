/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.graphics.Canvas;
import android.graphics.Rect;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.utils.Color;

public class WA3RingTarget extends CircularTargetBase {

    public static final int ID = 3;

    public WA3RingTarget() {
        super(ID, R.string.wa_3_ring);
        zones = 4;
        radius = new float[]{83, 167, 333, 500};
        colorFill = new int[]{Color.LEMON_YELLOW, Color.LEMON_YELLOW, Color.LEMON_YELLOW, Color.FLAMINGO_RED};
        colorStroke = new int[]{Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY,
                Color.DARK_GRAY};
        strokeWidth = new int[] {4, 4, 4, 4};
        zonePoints = new int[][]{{10, 10, 9, 8},
                {10, 9, 9, 8},
                {11, 10, 9, 8},
                {5, 5, 5, 4},
                {9, 9, 9, 7}};
        showAsX = new boolean[]{true, false, false, true, false};
        diameters = new Diameter[]{new Diameter(40, Dimension.CENTIMETER),
                new Diameter(60, Dimension.CENTIMETER),
                new Diameter(80, Dimension.CENTIMETER),
                new Diameter(92, Dimension.CENTIMETER),
                new Diameter(122, Dimension.CENTIMETER)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(Color.DARK_GRAY);
        final float size = recalc(rect, 16.667f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY(),
                rect.exactCenterX() + size, rect.exactCenterY(), paintStroke);
        canvas.drawLine(rect.exactCenterX(), rect.exactCenterY() - size,
                rect.exactCenterX(), rect.exactCenterY() + size, paintStroke);
    }
}
