package de.dreier.mytargets.shared.targets.zone;

import android.graphics.Canvas;

import de.dreier.mytargets.shared.models.Coordinate;

public class CircularZone extends ZoneBase {

    public CircularZone(float radius, int fillColor, int strokeColor, int strokeWidth) {
        this(radius, fillColor, strokeColor, strokeWidth, true);
    }

    public CircularZone(float radius, int fillColor, int strokeColor, int strokeWidth, boolean scoresAsOutsideIn) {
        this(radius, 0f, 0f, fillColor, strokeColor, strokeWidth, scoresAsOutsideIn);
    }

    public CircularZone(float radius, float midpointX, float midpointY, int fillColor, int strokeColor, int strokeWidth) {
        this(radius, midpointX, midpointY, fillColor, strokeColor, strokeWidth, true);
    }

    public CircularZone(float radius, float midpointX, float midpointY, int fillColor, int strokeColor, int strokeWidth, boolean scoresAsOutsideIn) {
        super(radius, new Coordinate(midpointX, midpointY), fillColor, strokeColor, strokeWidth, scoresAsOutsideIn);
    }

    @Override
    public boolean isInZone(float ax, float ay, float arrowRadius) {
        float distance = (ax - midpoint.x) * (ax - midpoint.x) + (ay - midpoint.y) * (ay - midpoint.y);
        float adaptedRadius = radius + (scoresAsOutsideIn ? 1f : -1f) * (arrowRadius + strokeWidth / 2.0f);
        return adaptedRadius * adaptedRadius > distance;
    }

    @Override
    public void draw(Canvas canvas) {
        initPaint();
        canvas.drawCircle(midpoint.x, midpoint.y, radius, paintFill);
        canvas.drawCircle(midpoint.x, midpoint.y, radius, paintStroke);
    }

}