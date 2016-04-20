package de.dreier.mytargets.shared.targets;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;

import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.utils.Color;

import static de.dreier.mytargets.shared.utils.Color.BLACK;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class TargetModelBase implements IIdProvider {
    public boolean isFieldTarget;
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
    protected Coordinate[] facePositions;

    protected boolean is3DTarget;
    protected CenterMark centerMark;

    protected TargetModelBase(long id, @StringRes int nameRes) {
        this.id = id;
        this.nameRes = nameRes;
        this.faceRadius = 500;
        this.facePositions = new Coordinate[]{new Coordinate(500, 500)};
    }

    @Override
    public long getId() {
        return id;
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

    public int getStrokeWidth(int zone) {
        return getZone(zone).strokeWidth;
    }

    public int getPointsByZone(int zone, int scoringStyle, int arrow) {
        if (isOutOfRange(zone)) {
            return 0;
        }
        return getZonePoints(scoringStyle, zone);
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

    @ColorInt
    public int getFillColor(int zone) {
        if (isOutOfRange(zone)) {
            return BLACK;
        }
        return getZone(zone).fillColor;
    }

    public int getStrokeColor(int zone) {
        // Handle Miss-shots
        if (isOutOfRange(zone)) {
            return BLACK;
        }
        return de.dreier.mytargets.shared.utils.Color.getStrokeColor(getZone(zone).fillColor);
    }

    public int getContrastColor(int zone) {
        // Handle Miss-shots
        if (isOutOfRange(zone)) {
            return BLACK;
        }
        return Color.getContrast(getZone(zone).fillColor);
    }

    public int getTextColor(int zone) {
        // Handle Miss-shots
        if (isOutOfRange(zone)) {
            return WHITE;
        }
        return Color.getContrast(getZone(zone).fillColor);
    }

    private boolean isOutOfRange(int zone) {
        return zone < 0 || zone >= getZoneCount();
    }

}
