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

import static android.graphics.Color.WHITE;

public class NFAAHunter extends CircularTargetBase {
    public static final int ID = 9;

    public NFAAHunter(Context context) {
        super(context, ID, R.string.nfaa_hunter);
        zones = 4;
        radius = new float[]{50, 100, 300, 500};
        colorFill = new int[]{WHITE, WHITE, DARK_GRAY, DARK_GRAY};
        colorStroke = new int[]{DARK_GRAY, WHITE, WHITE, DARK_GRAY};
        strokeWidth = new int[]{4, 0, 4, 0};
        zonePoints = new int[][]{{5, 5, 4, 3},
                {6, 5, 4, 3}};
        showAsX = new boolean[]{true, false};
        diameters = new Diameter[]{new Diameter(20, Dimension.CENTIMETER),
                new Diameter(35, Dimension.CENTIMETER),
                new Diameter(50, Dimension.CENTIMETER),
                new Diameter(65, Dimension.CENTIMETER)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(DARK_GRAY);
        final float size = recalc(rect, 7.307f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY() - size,
                rect.exactCenterX() + size, rect.exactCenterY() + size, paintStroke);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY() + size,
                rect.exactCenterX() + size, rect.exactCenterY() - size, paintStroke);
    }

    @Override
    public boolean isFieldTarget() {
        return true;
    }
}
