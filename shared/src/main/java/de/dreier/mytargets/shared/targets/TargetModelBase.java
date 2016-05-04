package de.dreier.mytargets.shared.targets;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;

import com.annimon.stream.Stream;

import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.utils.Color;

import static de.dreier.mytargets.shared.utils.Color.BLACK;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class TargetModelBase implements IIdProvider {
    public boolean isFieldTarget;
    protected long id;
    protected int nameRes;
    protected Zone[] zones;
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

    public Zone getZone(int zone) {
        return zones[zone];
    }

    public float getRadius(int zone) {
        return getZone(zone).radius;
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

    public int getPointsByZone(int zone, int scoringStyle, int arrow) {
        if (isOutOfRange(zone)) {
            return 0;
        }
        return getZonePoints(scoringStyle, zone);
    }

    public ArrayList<String> getScoringStyles() {
        ArrayList<String> styles = new ArrayList<>(getZoneCount());
        for (int scoring = 0; scoring < zonePoints.length; scoring++) {
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

    /**
     * Returns an x coordinate that lays inside the given zone
     *
     * @param zone Zone to get coordinate for
     * @return an x coordinate between 0 and 1 relative to the middle of the target face.
     */
    public float getXFromZone(int zone) {
        int zones = this.zones.length;
        if (zone < 0) {
            return (zones * 2 + 1) / (float) (zones * 2);
        } else if (zone == 0) {
            return (getZone(zone).midpoint.x - 500f) / 500f;
        } else {
            float adjacentZone = getRadius(zone - 1);
            float diff = Math.abs(getRadius(zone) - adjacentZone);
            return (getRadius(zone - 1) + diff / 2.0f) / 500.0f;
            //TODO test for non circular targets
        }
    }

    public Coordinate getCoordinateFromZone(int zone) {
        if (!isOutOfRange(zone) && getZone(zone).midpoint.y != 500) {
            final Coordinate midpoint = getZone(zone).midpoint;
            return new Coordinate((midpoint.x - 500f) / 500f, (midpoint.y - 500f) / 500f);
        }
        return new Coordinate(getXFromZone(zone), 500);
    }

    public int getMaxPoints(int scoringStyle) {
        return Stream.range(0, zonePoints.length)
                .map(i -> getZonePoints(scoringStyle, i))
                .max(Integer::compareTo).orElse(0);
    }

    public int getZoneFromPoint(float ax, float ay) {
        for (int i = 0; i < zones.length; i++) {
            if (getZone(i).isInZone(ax, ay)) {
                return i;
            }
        }
        return Shot.MISS;
    }
}
