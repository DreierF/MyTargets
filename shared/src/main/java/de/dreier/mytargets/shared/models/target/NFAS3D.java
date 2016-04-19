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

import static android.graphics.Color.BLACK;

public class NFAS3D extends Target3DBase {
    public NFAS3D() {
        super(18, R.string.nfas_3d);
        zones = 4;
        radius = new float[]{62, 209, 0, 500};
        colorFill = new int[]{RED, CERULEAN_BLUE, LIGHT_GRAY, BROWN};
        colorStroke = new int[]{BLACK, BLACK, BLACK, GRAY};
        strokeWidth = new int[]{3, 4, 3, 5};
        zonePoints = new int[][]{{24, 20, 16, 0}};
        showAsX = new boolean[]{false};
    }

    @Override
    public void drawZone(Canvas canvas, Rect rect, int zone) {
        switch (zone) {
            case 0:
                drawStrokeCircle(canvas, rect, 563.447f, 576.566f, 62);
                drawStrokeCircle(canvas, rect, 563.447f, 576.566f, 26.698f);
                break;
            default:
                super.drawZone(canvas, rect, zone);
                break;
        }
    }
}
