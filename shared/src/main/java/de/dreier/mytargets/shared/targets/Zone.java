package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.models.Coordinate;

public class Zone {
    final ZoneType type;
    final float radius;
    final Coordinate midpoint;
    final int fillColor;
    final int strokeColor;
    final int strokeWidth;

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
}