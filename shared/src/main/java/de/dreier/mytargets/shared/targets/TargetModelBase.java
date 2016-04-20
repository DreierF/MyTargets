package de.dreier.mytargets.shared.targets;

import android.content.Context;
import android.support.annotation.StringRes;

import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Diameter;

public class TargetModelBase {
    protected long id;
    protected int nameRes;
    protected Zone[] zones;
    protected boolean outsideIn = true;
    protected Diameter[] diameters;
    protected TargetDecoration decoration;
    protected boolean[] showAsX;
    protected int[][] zonePoints;
    protected boolean[] showPoints;

    protected int faceRadius;
    protected TargetDrawable face;
    protected Coordinate[] facePositions;

    protected boolean is3DTarget;
    public boolean isFieldTarget;
    protected CenterMark centerMark;


    protected TargetModelBase(long id, @StringRes int nameRes) {
        this.id = id;
        this.nameRes = nameRes;
        this.faceRadius = 500;
        this.facePositions = new Coordinate[] { new Coordinate(500,500) };
    }

    public String getName(Context context) {
        return context.getString(nameRes);
    }

    public ZoneType getZoneType(int zone) {
        return getZone(zone).type;
    }

    private Zone getZone(int zone) {
        return zones[zone];
    }

    public Coordinate getZoneMidpoint(int zone) {
        return getZone(zone).midpoint;
    }

    public float getRadius(int zone) {
        return getZone(zone).radius;
    }

    public boolean scoresAsOutSideIn(int zone) {
        return outsideIn;
    }

    public Diameter[] getDiameters() {
        return diameters;
    }

    public int getZonePoints(int scoringStyle, int i) {
        return zonePoints[scoringStyle][i];
    }

    public int getZoneCount() {
        return zones.length;
    }

    public int getFillColor(int zone) {
        return getZone(zone).fillColor;
    }

    public int getStrokeColor(int zone) {
        return getZone(zone).strokeColor;
    }

    public int getStrokeWidth(int zone) {
        return getZone(zone).strokeWidth;
    }

    public int getPointsByZone(int zone, int scoringStyle, int arrow) {
        return 0; //TODO
    }

    public ArrayList<String> getScoringStyles() {
        ArrayList<String> styles = new ArrayList<>(getZoneCount());
        for (int scoring = 0; scoring < getZoneCount(); scoring++) {
            String style = "";
            for (int i = 0; i < getZoneCount(); i++) {
                if (!style.isEmpty()) {
                    style += ", ";
                }
                if (i == 0 && zonePoints[scoring][0] < zonePoints[scoring][1]) {
                    continue;
                }
                style += zoneToString(i, scoring, 0);
            }
            styles.add(style);
        }
        return styles;
    }

    public String zoneToString(int zone, int scoringStyle, int arrow) {
        if (zone <= -1 || zone >= getZoneCount()) {
            return "M";
        } else if (zone == 0 && showAsX[scoringStyle]) {
            return "X";
        } else {
            int value = getPointsByZone(zone, scoringStyle, arrow);
            if (value == 0) {
                return "M";
            }
            return String.valueOf(value);
        }
    }

    public boolean dependsOnArrowIndex() {
        return false;
    }

    public boolean is3DTarget() {
        return is3DTarget;
    }

    public boolean isFieldTarget() {
        return isFieldTarget;
    }

    public int getFaceCount() {
        return facePositions.length;
    }

    public TargetDecoration getDecoration() {
        return decoration;
    }

    public boolean shouldDrawZone(int zone, int scoringStyle) {
        return true;
    }

    public CenterMark getCenterMark() {
        return centerMark;
    }
}
