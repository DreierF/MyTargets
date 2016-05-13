package de.dreier.mytargets.shared.targets;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

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
    protected ScoringStyle[] scoringStyles;
    protected int faceRadius;
    protected Coordinate[] facePositions;
    protected boolean is3DTarget;
    protected CenterMark centerMark;
    protected TargetDecoration decoration;

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

    public int getZoneCount() {
        return zones.length;
    }

    public List<String> getScoringStyles() {
        return Stream.of(scoringStyles)
                .map(ScoringStyle::toString)
                .collect(Collectors.toList());
    }

    public ScoringStyle getScoringStyle(int scoringStyle) {
        return scoringStyles[scoringStyle];
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

    // TODO Make a dummy miss zone to outsource color stuff
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

    public int getZoneFromPoint(float ax, float ay) {
        for (int i = 0; i < zones.length; i++) {
            if (getZone(i).isInZone(ax, ay)) {
                return i;
            }
        }
        return Shot.MISS;
    }

    public List<SelectableZone> getSelectableZoneList(int scoringStyle, int arrow) {
        List<SelectableZone> list = new ArrayList<>();
        String last = "";
        for (int i = 0; i < getZoneCount(); i++) {
            String zone = getScoringStyle(scoringStyle).zoneToString(i, arrow);
            if (!last.equals(zone)) {
                list.add(new SelectableZone(i, zone));
            }
            last = zone;
        }
        if (!last.equals("M")) {
            list.add(new SelectableZone(-1, "M"));
        }
        return list;
    }

    public static class SelectableZone {
        public final int zone;
        public final String text;

        public SelectableZone(int zone, String text) {
            this.zone = zone;
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof SelectableZone && zone == ((SelectableZone) o).zone;
        }
    }
}
