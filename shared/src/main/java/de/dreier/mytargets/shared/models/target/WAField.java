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

import static android.graphics.Color.WHITE;

public class WAField extends CircularTargetBase {
    public static final int ID = 13;

    public WAField() {
        super(ID, R.string.wa_field);
        zones = 6;
        radius = new float[]{50, 100, 200, 300, 400, 500};
        colorFill = new int[]{Color.LEMON_YELLOW, Color.LEMON_YELLOW, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY};
        colorStroke = new int[]{Color.DARK_GRAY, Color.DARK_GRAY, WHITE, WHITE, WHITE, WHITE};
        strokeWidth = new int[]{4, 4, 4, 4, 4, 4};
        zonePoints = new int[][]{{5, 5, 4, 3, 2, 1},
                {6, 5, 4, 3, 2, 1}};
        showAsX = new boolean[]{true, false};
        diameters = new Diameter[]{new Diameter(20, Dimension.CENTIMETER),
                new Diameter(40, Dimension.CENTIMETER),
                new Diameter(60, Dimension.CENTIMETER),
                new Diameter(80, Dimension.CENTIMETER)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(Color.DARK_GRAY);
        final float size = recalc(rect, 10.5f);
        paintStroke.setStrokeWidth(4 * rect.width() / 1000f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY(),
                rect.exactCenterX() + size, rect.exactCenterY(), paintStroke);
        canvas.drawLine(rect.exactCenterX(), rect.exactCenterY() - size,
                rect.exactCenterX(), rect.exactCenterY() + size, paintStroke);
    }

    @Override
    public boolean isFieldTarget() {
        return true;
    }
}
