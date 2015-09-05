/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.StringRes;

import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Diameter;

public class TargetOvalBase extends Target {

    private static final Path path = new Path();
    private static final Region region;

    static {
        path.moveTo(670, 252.5f);
        path.arcTo(new RectF(94.5f, 252.5f, 332 + (332 - 94.5f), 749.5f), -90, -180, false);
        path.arcTo(new RectF(670 - (332 - 94.5f), 252.5f, 670 + (332 - 94.5f), 749.5f), 90, -180,
                false);
        path.close();
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        region = new Region();
        region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right,
                (int) rectF.bottom));
    }

    public TargetOvalBase(Context context, long id, @StringRes int name) {
        super(context, id, name);
    }

    @Override
    public void drawZone(Canvas canvas, Rect rect, int zone) {
        if (zones - zone == 2) {
            drawStrokePath(canvas, rect, path);
        } else {
            super.drawZone(canvas, rect, zone);

        }
    }

    @Override
    protected boolean isInZone(float ax, float ay, int zone, boolean outsideIn) {
        return zones - zone == 2 && region.contains((int) ax, (int) ay);
    }

    @Override
    public Diameter[] getDiameters() {
        return new Diameter[]{Diameter.SMALL,
                Diameter.MEDIUM,
                Diameter.LARGE,
                Diameter.XLARGE};
    }

    @Override
    protected int getPointsByZone(int zone, int scoring, int arrow) {
        if (zone == -1 || zone >= zones) {
            return 0;
        }
        return zonePoints[arrow < zonePoints.length ? arrow : zonePoints.length - 1][zone];
    }

    @Override
    public ArrayList<String> getScoringStyles() {
        //TODO consider 2nd and 3rd points
        ArrayList<String> styles = new ArrayList<>(1);
        String style = "";
        for (int i = 0; i < zones; i++) {
            if (!style.isEmpty()) {
                style += ", ";
            }
            style += zoneToString(i, 0, 0);
        }
        styles.add(style);
        return styles;
    }

    @Override
    public boolean dependsOnArrowIndex() {
        return true;
    }

    @Override
    public boolean is3DTarget() {
        return true;
    }
}
