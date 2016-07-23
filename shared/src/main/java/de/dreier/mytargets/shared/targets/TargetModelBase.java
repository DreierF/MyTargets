package de.dreier.mytargets.shared.targets;

import android.support.annotation.StringRes;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.SharedApplicationInstance;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.utils.Color;

import static de.dreier.mytargets.shared.utils.Color.BLACK;

public class TargetModelBase implements IIdProvider {
    private final int nameRes;
    boolean isFieldTarget;
    long id;
    Zone[] zones;
    Dimension[] diameters;
    ScoringStyle[] scoringStyles;
    int faceRadius;
    Coordinate[] facePositions;
    boolean is3DTarget;
    CenterMark centerMark;
    TargetDecoration decoration;

    TargetModelBase(long id, @StringRes int nameRes) {
        this.id = id;
        this.nameRes = nameRes;
        this.faceRadius = 500;
        this.facePositions = new Coordinate[]{new Coordinate(500, 500)};
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return SharedApplicationInstance.get(nameRes);
    }

    public Zone getZone(int zone) {
        if (isOutOfRange(zone)) {
            return new Zone(0, BLACK, BLACK, 0);
        }
        return zones[zone];
    }

    public Dimension[] getDiameters() {
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

    public int getContrastColor(int zone) {
        // Handle Miss-shots
        if (isOutOfRange(zone)) {
            return BLACK;
        }
        return Color.getContrast(getZone(zone).fillColor);
    }

    public int getTextColor(int zone) {
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

    public List<SelectableZone> getSelectableZoneList(int scoringStyleIndex, int arrow) {
        ScoringStyle scoringStyle = getScoringStyle(scoringStyleIndex);
        List<SelectableZone> list = new ArrayList<>();
        String last = "";
        for (int i = 0; i <= zones.length; i++) {
            String zone = scoringStyle.zoneToString(i, arrow);
            if (!last.equals(zone)) {
                list.add(new SelectableZone(i == zones.length ? -1 : i, getZone(i), zone, scoringStyle.getPointsByZone(i, arrow)));
            }
            last = zone;
        }
        return list;
    }

}
