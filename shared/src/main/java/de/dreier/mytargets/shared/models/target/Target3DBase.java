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

import de.dreier.mytargets.shared.models.Diameter;

public class Target3DBase extends Target {

    private static final Path path = new Path();
    private static final Region region;

    static {
        path.moveTo(296.724f, 162.855f);
        path.cubicTo(335.067f, 163.204f, 373.244f, 166.065f, 411.339f, 170.284f);
        path.cubicTo(456.474f, 175.282f, 501.268f, 182.376f, 545.813f, 191.214f);
        path.cubicTo(603.462f, 202.651f, 659.337f, 219.971f, 714.041f, 241.221f);
        path.cubicTo(758.396f, 258.451f, 801.187f, 278.853f, 840.953f, 305.206f);
        path.cubicTo(878.816f, 330.298f, 912.56f, 359.766f, 937.666f, 398.081f);
        path.cubicTo(963.709f, 437.826f, 977.903f, 481.424f, 977.803f, 529.132f);
        path.cubicTo(977.729f, 564.625f, 967.457f, 597.725f, 952.022f, 629.375f);
        path.cubicTo(932.967f, 668.448f, 907.063f, 702.686f, 877.3f, 734.084f);
        path.cubicTo(819.307f, 795.264f, 751.654f, 843.58f, 677.4f, 882.921f);
        path.cubicTo(636.841f, 904.41f, 594.784f, 922.353f, 550.413f, 934.514f);
        path.cubicTo(528.327f, 940.568f, 505.912f, 944.696f, 482.973f, 945.587f);
        path.cubicTo(457.304f, 946.582f, 433.869f, 938.534f, 411.189f, 927.642f);
        path.cubicTo(370.663f, 908.181f, 337.503f, 879.354f, 308.084f, 845.893f);
        path.cubicTo(273.747f, 806.837f, 246.171f, 763.221f, 222.543f, 717.076f);
        path.cubicTo(176.966f, 628.063f, 145.785f, 534.017f, 125.222f, 436.309f);
        path.cubicTo(117.673f, 400.437f, 111.934f, 364.285f, 108.819f, 327.757f);
        path.cubicTo(106.461f, 300.103f, 105.353f, 272.386f, 108.692f, 244.734f);
        path.cubicTo(109.772f, 235.791f, 111.818f, 226.933f, 113.908f, 218.152f);
        path.cubicTo(116.691f, 206.465f, 123.898f, 197.894f, 133.807f, 191.308f);
        path.cubicTo(149.769f, 180.7f, 167.684f, 175.263f, 186.146f, 171.335f);
        path.cubicTo(214.046f, 165.399f, 242.359f, 163.326f, 270.816f, 162.875f);
        path.cubicTo(279.451f, 162.739f, 288.089f, 162.855f, 296.724f, 162.855f);
        path.close();
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        region = new Region();
        region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right,
                (int) rectF.bottom));
    }

    public Target3DBase(Context context, long id, @StringRes int name) {
        super(context, id, name);
    }

    @Override
    public void drawZone(Canvas canvas, Rect rect, int zone) {
        switch (zones - zone) {
            case 2:
                drawStrokePath(canvas, rect, path);
                break;
            case 3:
                drawStrokeCircle(canvas, rect, 564.375f, 577.813f, 208.938f);
                break;
            default:
                super.drawZone(canvas, rect, zone);
                break;
        }
    }

    @Override
    protected boolean isInZone(float ax, float ay, int zone, boolean outsideIn) {
        if (zones - zone == 2) {
            return region.contains((int) ax, (int) ay);
        } else if (zones - zone == 3) {
            return (ax - 564.375f) * (ax - 564.375f) + (ay - 577.813f) * (ay - 577.813f) <
                    43655.09f;
        }
        return false;
    }

    @Override
    public Diameter[] getDiameters() {
        return new Diameter[]{Diameter.MINI,
                Diameter.SMALL,
                Diameter.MEDIUM,
                Diameter.LARGE,
                Diameter.XLARGE};
    }

    @Override
    public boolean is3DTarget() {
        return true;
    }

    @Override
    public int getMaxPoints() {
        return Math.max(zonePoints[scoringStyle][0], zonePoints[scoringStyle][1]);
    }
}
