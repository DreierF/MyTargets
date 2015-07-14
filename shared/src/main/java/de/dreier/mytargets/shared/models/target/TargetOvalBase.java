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
import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;

public class TargetOvalBase extends Target {

    private static final Path path = new Path();
    private static final Region region;

    static {
        path.moveTo(670, 252.5f);
        path.arcTo(new RectF(94.5f, 252.5f, 332+(332-94.5f), 749.5f), -90, -180, false);
        path.arcTo(new RectF(670 - (332-94.5f), 252.5f, 670 + (332-94.5f), 749.5f), 90, -180, false);
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
    protected boolean isInZone(float ax, float ay, int zone) {
        return zones - zone == 2 && region.contains((int) ax, (int) ay);
    }

    @Override
    public Diameter[] getDiameters(Context context) {
        return new Diameter[] {new Diameter(context,R.string.small),
                new Diameter(context,R.string.medium),
                new Diameter(context,R.string.large),
                new Diameter(context,R.string.xlarge)};
    }
}
