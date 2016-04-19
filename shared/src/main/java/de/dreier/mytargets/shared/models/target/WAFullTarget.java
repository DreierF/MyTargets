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

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class WAFullTarget extends CircularTargetBase {

    public static final int ID = 0;

    public WAFullTarget() {
        super(ID, R.string.wa_full);
        zones = 11;
        radius = new float[]{25, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500};
        colorFill = new int[]{Color.LEMON_YELLOW, Color.LEMON_YELLOW, Color.LEMON_YELLOW, Color.FLAMINGO_RED, Color.FLAMINGO_RED,
                Color.CERULEAN_BLUE, Color.CERULEAN_BLUE, BLACK, BLACK, WHITE,
                WHITE};
        colorStroke = new int[]{Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY,
                Color.DARK_GRAY, Color.DARK_GRAY,
                Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY,
                Color.DARK_GRAY, Color.DARK_GRAY};
        strokeWidth = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        zonePoints = new int[][]{{10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                {10, 9, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                {11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                {5, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1},
                {9, 9, 9, 7, 7, 5, 5, 3, 3, 1, 1}};
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
        final float size = recalc(rect, 5);
        paintStroke.setStrokeWidth(4 * rect.width() / 1000f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY(),
                rect.exactCenterX() + size, rect.exactCenterY(), paintStroke);
        canvas.drawLine(rect.exactCenterX(), rect.exactCenterY() - size,
                rect.exactCenterX(), rect.exactCenterY() + size, paintStroke);
    }
}
