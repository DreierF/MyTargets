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

public class NFAAExpertField extends CircularTargetBase {

    public NFAAExpertField(Context context) {
        super(context, 8, R.string.nfaa_expert_field);
        zones = 6;
        radius = new int[]{50, 100, 200, 300, 400, 500};
        colorFill = new int[]{DARK_GRAY, DARK_GRAY, WHITE, WHITE, DARK_GRAY, DARK_GRAY};
        colorStroke = new int[]{WHITE, DARK_GRAY, DARK_GRAY, WHITE, WHITE, DARK_GRAY};
        strokeWidth = new int[]{4, 0, 4, 0, 4, 0};
        zonePoints = new int[][]{{6, 5, 4, 3, 2, 1}};
        showAsX = new boolean[]{true};
        diameters = new Dimension[]{new Dimension(20, Dimension.CENTIMETER),
                new Dimension(35, Dimension.CENTIMETER),
                new Dimension(50, Dimension.CENTIMETER),
                new Dimension(65, Dimension.CENTIMETER)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(WHITE);
        final float size = recalc(rect, 7.307f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY() - size,
                rect.exactCenterX() + size, rect.exactCenterY() + size, paintStroke);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY() + size,
                rect.exactCenterX() + size, rect.exactCenterY() - size, paintStroke);
    }
}