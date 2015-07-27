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

public class WAField extends CircularTargetBase {
    public static final int ID = 13;

    public WAField(Context context) {
        super(context, ID, R.string.wa_field);
        zones = 6;
        radius = new float[]{50, 100, 200, 300, 400, 500};
        colorFill = new int[]{LEMON_YELLOW, LEMON_YELLOW, DARK_GRAY, DARK_GRAY, DARK_GRAY, DARK_GRAY};
        colorStroke = new int[]{Target.DARK_GRAY, Target.DARK_GRAY, WHITE, WHITE, WHITE, WHITE};
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
        paintStroke.setColor(Target.DARK_GRAY);
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
