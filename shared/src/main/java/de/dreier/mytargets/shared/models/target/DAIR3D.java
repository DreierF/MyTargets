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

public class DAIR3D extends Target3DBase {
    public static final int ID = 19;

    public DAIR3D(Context context) {
        super(context, ID, R.string.dair_3d);
        zones = 6;
        radius = new float[]{0, 0, 0, 0, 0, 500};
        colorFill = new int[]{GREEN, TURBO_YELLOW, RED, CERULEAN_BLUE, LIGHT_GRAY, BROWN};
        colorStroke = new int[]{BLACK, BLACK, BLACK, BLACK, BLACK, GRAY};
        strokeWidth = new int[]{3, 3, 3, 4, 3, 5};
        zonePoints = new int[][]{
                {8, 12, 11, 10, 8, 0},
                {14, 12, 11, 10, 8, 0},
                {8, 12, 10, 10, 8, 0},
                {14, 12, 10, 10, 8, 0}};
        showAsX = new boolean[]{false, false, false, false};
    }

    @Override
    public void drawZone(Canvas canvas, Rect rect, int zone) {
        switch (zone) {
            case 0:
                drawStrokeCircle(canvas, rect, 184.448f, 250.568f, 62);
                drawStrokeCircle(canvas, rect, 184.448f, 250.568f, 26.698f);
                break;
            case 1:
                drawStrokeCircle(canvas, rect, 637.198f, 455.401f, 62);
                drawStrokeCircle(canvas, rect, 637.198f, 455.401f, 26.698f);
                drawStrokeCircle(canvas, rect, 491.448f, 699.901f, 62);
                drawStrokeCircle(canvas, rect, 491.448f, 699.901f, 26.698f);
                break;
            case 2:
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
                return (ax - 184.448f) * (ax - 184.448f) + (ay - 250.568f) * (ay - 250.568f) < 3844;
            case 1:
                return (ax - 637.198f) * (ax - 637.198f) + (ay - 455.401f) * (ay - 455.401f) <
                        3844 ||
                        (ax - 491.448f) * (ax - 491.448f) + (ay - 699.901f) * (ay - 699.901f) <
                                3844;
            case 2:
                return (ax - 563.447f) * (ax - 563.447f) + (ay - 576.566f) * (ay - 576.566f) < 3844;
            default:
                return super.isInZone(ax, ay, zone, outsideIn);
        }
    }
}
