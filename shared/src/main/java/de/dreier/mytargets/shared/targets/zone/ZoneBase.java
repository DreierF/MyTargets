package de.dreier.mytargets.shared.targets.zone;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.utils.Color;

public abstract class ZoneBase {
    public final float radius;
    public final int fillColor;
    public final int strokeColor;
    public final float strokeWidth;
    protected final Coordinate midpoint;
    protected final boolean scoresAsOutsideIn;

    Paint paintFill;
    Paint paintStroke;

    public ZoneBase(float radius, Coordinate midpoint, int fillColor, int strokeColor, int strokeWidth, boolean scoresAsOutsideIn) {
        this.radius = radius;
        this.midpoint = midpoint;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth * 0.001f;
        this.scoresAsOutsideIn = scoresAsOutsideIn;
    }

    protected void initPaint() {
        if (paintFill != null) {
            return;
        }
        paintFill = new Paint();
        paintFill.setAntiAlias(true);
        paintFill.setColor(fillColor);
        paintStroke = new Paint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAntiAlias(true);
        paintStroke.setColor(strokeColor);
        paintStroke.setStrokeWidth(strokeWidth);
    }

    public abstract boolean isInZone(float ax, float ay, float arrowRadius);

    public int getFillColor() {
        return fillColor;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public int getTextColor() {
        return Color.getContrast(fillColor);
    }

    public abstract void drawFill(Canvas canvas);
    public abstract void drawStroke(Canvas canvas);

    /**
     * Workaround for http://stackoverflow.com/questions/16090607/blurry-offset-paths-when-canvas-is-scaled-under-hardware-acceleration
     */
    protected void drawPath(Canvas canvas, Path path, final Paint pen) {
        canvas.save();

        // get the current matrix
        Matrix mat = canvas.getMatrix();

        // reverse the effects of the current matrix
        Matrix inv = new Matrix();
        mat.invert(inv);
        canvas.concat(inv);

        // transform the path
        path.transform(mat);

        // get the scale for transforming the Paint
        float[] pts = {0, 0, 1, 0}; // two points 1 unit away from each other
        mat.mapPoints(pts);
        float scale = (float) Math.sqrt(Math.pow(pts[0] - pts[2], 2) + Math.pow(pts[1] - pts[3], 2));

        // copy the existing Paint
        Paint pen2 = new Paint();
        pen2.set(pen);

        // scale the Paint
        pen2.setStrokeMiter(pen.getStrokeMiter() * scale);
        pen2.setStrokeWidth(pen.getStrokeWidth() * scale);

        // draw the path
        canvas.drawPath(path, pen2);
        path.transform(inv);

        canvas.restore();
    }
}
