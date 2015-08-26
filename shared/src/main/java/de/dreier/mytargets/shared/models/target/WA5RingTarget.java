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

public class WA5RingTarget extends CircularTargetBase {

    public static final int ID = 2;
    private boolean usedAsSpot = false;

    public WA5RingTarget(Context context) {
        super(context, ID, R.string.wa_5_ring);
        zones = 6;
        radius = new float[]{50, 100, 200, 300, 400, 500};
        colorFill = new int[]{LEMON_YELLOW, LEMON_YELLOW, LEMON_YELLOW, FLAMINGO_RED, FLAMINGO_RED,
                CERULEAN_BLUE};
        colorStroke = new int[]{Target.DARK_GRAY, Target.DARK_GRAY, Target.DARK_GRAY,
                Target.DARK_GRAY, Target.DARK_GRAY, Target.DARK_GRAY};
        strokeWidth = new int[] {4, 4, 4, 4, 4, 4};
        zonePoints = new int[][]{{10, 10, 9, 8, 7, 6},
                {10, 9, 9, 8, 7, 6},
                {11, 10, 9, 8, 7, 6},
                {5, 5, 5, 4, 4, 3},
                {9, 9, 9, 7, 7, 5}};
        showAsX = new boolean[]{true, false, false, true, false};
        diameters = new Diameter[]{new Diameter(40, Dimension.CENTIMETER),
                new Diameter(60, Dimension.CENTIMETER),
                new Diameter(80, Dimension.CENTIMETER),
                new Diameter(92, Dimension.CENTIMETER),
                new Diameter(122, Dimension.CENTIMETER)};
    }

    public WA5RingTarget(Context context, boolean usedAsSpot) {
        this(context);
        this.usedAsSpot = usedAsSpot;
    }

    @Override
    protected void drawZone(Canvas canvas, Rect rect, int zone) {
        // Do not draw second ring if we have a 3 Spot for compound
        if (!usedAsSpot || scoringStyle != 1 || zone != 1) {
            super.drawZone(canvas, rect, zone);
        }
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(Target.DARK_GRAY);
        final float size = recalc(rect,10);
        paintStroke.setStrokeWidth(4 * rect.width() / 1000f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY(),
                rect.exactCenterX() + size, rect.exactCenterY(), paintStroke);
        canvas.drawLine(rect.exactCenterX(), rect.exactCenterY() - size,
                rect.exactCenterX(), rect.exactCenterY() + size, paintStroke);
    }
}
