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

import static android.graphics.Color.BLACK;

public class IBO3D extends Target3DBase {
    public IBO3D(Context context) {
        super(context, 17, R.string.ibo_3d);
        zones = 4;
        radius = new float[]{0, 0, 0, 500};
        colorFill = new int[]{RED, CERULEAN_BLUE, LIGHT_GRAY, BROWN};
        colorStroke = new int[]{BLACK, BLACK, BLACK, GRAY};
        strokeWidth = new int[]{3, 4, 3, 5};
        zonePoints = new int[][]{{11, 10, 8, 5}};
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

    @Override
    protected boolean isInZone(float ax, float ay, int zone, boolean outsideIn) {
        switch (zone) {
            case 0:
                return (ax - 563.447f) * (ax - 563.447f) + (ay - 576.566f) * (ay - 576.566f) < 3844;
            default:
                return super.isInZone(ax, ay, zone, outsideIn);
        }
    }
}
