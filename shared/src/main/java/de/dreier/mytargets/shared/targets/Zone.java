package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.models.Coordinate;

public class Zone {
    final ZoneType type;
    final float radius;
    final Coordinate midpoint;
    final int fillColor;
    final int strokeColor;
    final int strokeWidth;
    private final boolean scoresAsOutsideIn = true;

    public Zone(float radius, int fillColor, int strokeColor, int strokeWidth) {
        this.type = ZoneType.CIRCLE;
        this.radius = radius;
        this.midpoint = new Coordinate(500, 500);
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }

    public Zone(ZoneType type, float radius, float midpointX, float midpointY, int fillColor, int strokeColor, int strokeWidth) {
        this.type = type;
        this.radius = radius;
        this.midpoint = new Coordinate(midpointX, midpointY);
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }

    /**
     * @param ax        x-Coordinate scaled to a 0 ... 1000 coordinate system
     * @param ay        y-Coordinate scaled to a 0 ... 1000 coordinate system
     * @return
     */
    boolean isInZone(float ax, float ay) {
        switch (type) {
            case CIRCLE:
                float distance = (ax - midpoint.x) * (ax - midpoint.x) + (ay - midpoint.y) * (ay - midpoint.y);
                float adaptedRadius = radius +
                        (scoresAsOutsideIn ? TargetDrawable.ARROW_RADIUS + strokeWidth / 2.0f : -TargetDrawable.ARROW_RADIUS);
                return adaptedRadius * adaptedRadius > distance;
            case HEART:
                return TargetDrawable.HEART_REGION.contains((int) ax, (int) ay);
            case ELLIPSE:
                return TargetDrawable.ELLIPSE_REGION.contains((int) ax, (int) ay);
        }
        return false;
    }
}