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

import static android.graphics.Color.WHITE;

public class NFAAIndoor extends CircularTargetBase {

    public NFAAIndoor(Context context) {
        super(context, 10, R.string.nfaa_indoor);
        zones = 6;
        radius = new int[]{50, 100, 200, 300, 400, 500};
        colorFill = new int[]{WHITE, WHITE, SAPPHIRE_BLUE, SAPPHIRE_BLUE, SAPPHIRE_BLUE,
                SAPPHIRE_BLUE};
        colorStroke = new int[]{DARK_GRAY, DARK_GRAY, WHITE, WHITE, WHITE, WHITE};
        strokeWidth = new int[]{4, 0, 4, 4, 4, 4};
        zonePoints = new int[][]{{5, 5, 4, 3, 2, 1},
                {6, 5, 4, 3, 2, 1},
                {7, 5, 4, 3, 2, 1}}; //TODO 6 if inner ring is hit but only 7 if arrow is inside
        showAsX = new boolean[]{true, false, false};
        diameters = new Dimension[]{new Dimension(40, Dimension.CENTIMETER)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(DARK_GRAY);
        final float size = recalc(rect, 23.783f);
        paintStroke.setStrokeWidth(8 * rect.width() / 1000f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY() - size,
                rect.exactCenterX() + size, rect.exactCenterY() + size, paintStroke);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY() + size,
                rect.exactCenterX() + size, rect.exactCenterY() - size, paintStroke);
    }
}
