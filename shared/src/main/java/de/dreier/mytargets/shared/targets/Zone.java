package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.utils.Color;

public class Zone {
    final ZoneType type;
    final float radius;
    final Coordinate midpoint;
    final int fillColor;
    final int strokeColor;
    final int strokeWidth;
    final boolean scoresAsOutsideIn;

    public Zone(float radius, int fillColor, int strokeColor, int strokeWidth) {
        this(radius, fillColor, strokeColor, strokeWidth, true);
    }

    public Zone(float radius, int fillColor, int strokeColor, int strokeWidth, boolean scoresAsOutsideIn) {
        this.type = ZoneType.CIRCLE;
        this.radius = radius;
        this.midpoint = new Coordinate(500, 500);
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.scoresAsOutsideIn = scoresAsOutsideIn;
    }

    public Zone(ZoneType type, float radius, float midpointX, float midpointY, int fillColor, int strokeColor, int strokeWidth) {
        this.type = type;
        this.radius = radius;
        this.midpoint = new Coordinate(midpointX, midpointY);
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.scoresAsOutsideIn = true;
    }

    /**
     * @param ax          x-Coordinate scaled to a 0 ... 1000 coordinate system
     * @param ay          y-Coordinate scaled to a 0 ... 1000 coordinate system
     * @param arrowRadius Radius of the arrow, which is needed to determine if the arrow already touches the zones stroke.
     * @return Returns true if the given coordinates are within the zone.
     */
    boolean isInZone(float ax, float ay, float arrowRadius) {
        switch (type) {
            case CIRCLE:
                float distance = (ax - midpoint.x) * (ax - midpoint.x) + (ay - midpoint.y) * (ay - midpoint.y);
                float adaptedRadius = radius + (scoresAsOutsideIn ? 1f : -1f) * (arrowRadius + strokeWidth / 2.0f);
                return adaptedRadius * adaptedRadius > distance;
            case HEART:
                return TargetDrawable.HEART_REGION.contains((int) ax, (int) ay);
            case ELLIPSE:
                return TargetDrawable.ELLIPSE_REGION.contains((int) ax, (int) ay);
        }
        return false;
    }

    public int getFillColor() {
        return fillColor;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public int getTextColor() {
        return Color.getContrast(fillColor);
    }
}