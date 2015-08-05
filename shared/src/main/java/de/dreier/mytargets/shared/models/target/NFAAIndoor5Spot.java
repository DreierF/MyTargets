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

public class NFAAIndoor5Spot extends SpotBase {
    public NFAAIndoor5Spot(Context context) {
        super(context, 11, R.string.nfaa_indoor_5_spot);
        face = new NFAAIndoorSingleSpot(context);
        facePositions = new int[][]{{200, 200}, {800, 200},
                {500, 500}, {200, 800}, {800, 800}};
        faceRadius = 200;
    }

    private class NFAAIndoorSingleSpot extends CircularTargetBase {
        public NFAAIndoorSingleSpot(Context context) {
            super(context, -1, R.string.nfaa_indoor_5_spot);
            zones = 4;
            radius = new float[]{125, 250, 375, 500};
            colorFill = new int[]{WHITE, WHITE, SAPPHIRE_BLUE, SAPPHIRE_BLUE};
            colorStroke = new int[]{DARK_GRAY, DARK_GRAY, WHITE, WHITE};
            strokeWidth = new int[]{2, 2, 2, 0};
            zonePoints = new int[][]{{5, 5, 5, 4},
                    {6, 6, 5, 4},
                    {7, 6, 5, 4}}; //TODO 6 if inner ring is hit but only 7 if arrow is inside
            showAsX = new boolean[]{true, false, false};
            diameters = new Diameter[]{new Diameter(40, Dimension.CENTIMETER)};
        }

        @Override
        protected void onPostDraw(Canvas canvas, Rect rect) {
            paintStroke.setColor(DARK_GRAY);
            final float size = recalc(rect, 25f);
            paintStroke.setStrokeWidth(9 * rect.width() / 1000f);
            canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY() - size,
                    rect.exactCenterX() + size, rect.exactCenterY() + size, paintStroke);
            canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY() + size,
                    rect.exactCenterX() + size, rect.exactCenterY() - size, paintStroke);
        }
    }
    
    @Override
    public Diameter[] getDiameters() {
        return face.getDiameters();
    }
}
