/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import android.graphics.Canvas;
import android.graphics.Rect;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.utils.Color;

import static android.graphics.Color.WHITE;

public class Worcester extends TargetModelBase {
    public static final int ID = 12;

    public Worcester() {
        super((long) ID, R.string.worcester_face);
        zones = 5;
        zone.radius = new float[]{100, 200, 300, 400, 500};
        zone.fillColor = new int[]{WHITE, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY};
        zone.strokeColor = new int[]{WHITE, WHITE, WHITE, WHITE, WHITE};
        zone.strokeWidth = new int[]{4, 4, 4, 4, 0};
        zone.zonePoints = new int[][]{{5, 4, 3, 2, 1}};
        zone.showAsX = new boolean[]{false};
        diameters = new Diameter[]{new Diameter(16, Dimension.INCH)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(Color.DARK_GRAY);
        final float size = reCalc(rect, 10.5f);
        paintStroke.setStrokeWidth(4 * rect.width() / 1000f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY(),
                rect.exactCenterX() + size, rect.exactCenterY(), paintStroke);
        canvas.drawLine(rect.exactCenterX(), rect.exactCenterY() - size,
                rect.exactCenterX(), rect.exactCenterY() + size, paintStroke);
    }
}
