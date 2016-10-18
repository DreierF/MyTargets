package de.dreier.mytargets.shared.targets.zone;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.utils.RegionUtils;

public class EllipseZone extends ZoneBase {

    private static final float REGION_SCALE_FACTOR = 1000f;
    private static final Region ELLIPSE_REGION;
    private static final Path ellipse = new Path();

    static {
        ellipse.moveTo(0.34f, -0.495f);
        ellipse.arcTo(new RectF(-0.811f, -0.495f, 0.139f, 0.499f), -90, -180, false);
        ellipse.arcTo(new RectF(-0.135f, -0.495f, 0.815f, 0.499f), 90, -180, false);
        ellipse.close();

        /** The region needs to be bigger, because the Region#contains(x,y) only allows to test for
         * integers, which is obviously to inaccurate for a -1..1 coordinate system. */
        ELLIPSE_REGION = RegionUtils.getScaledRegion(ellipse, REGION_SCALE_FACTOR);
    }

    public EllipseZone(float radius, float midpointX, float midpointY, int fillColor, int strokeColor, int strokeWidth) {
        super(radius, new Coordinate(midpointX, midpointY), fillColor, strokeColor, strokeWidth, true);
    }

    @Override
    public boolean isInZone(float ax, float ay, float arrowRadius) {
        return ELLIPSE_REGION.contains((int) (ax * REGION_SCALE_FACTOR), (int) (ay * REGION_SCALE_FACTOR));
    }

    @Override
    public void drawFill(Canvas canvas) {
        initPaint();
        drawPath(canvas, ellipse, paintFill);
    }

    @Override
    public void drawStroke(Canvas canvas) {
        initPaint();
        drawPath(canvas, ellipse, paintStroke);
    }
}