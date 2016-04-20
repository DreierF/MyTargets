/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import android.support.annotation.StringRes;

import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Diameter;

import static de.dreier.mytargets.shared.models.Diameter.LARGE;
import static de.dreier.mytargets.shared.models.Diameter.MEDIUM;
import static de.dreier.mytargets.shared.models.Diameter.SMALL;
import static de.dreier.mytargets.shared.models.Diameter.XLARGE;

public class TargetOvalBase extends TargetModelBase {

    public TargetOvalBase(long id, @StringRes int name) {
        super(id, name);
        diameters = new Diameter[]{SMALL, MEDIUM, LARGE, XLARGE};
    }

    @Override
    public int getPointsByZone(int zone, int scoring, int arrow) {
        if (zone == -1 || zone >= zones.length) {
            return 0;
        }
        return zonePoints[arrow < zonePoints.length ? arrow : zonePoints.length - 1][zone];
    }

    @Override
    public ArrayList<String> getScoringStyles() {
        //TODO consider 2nd and 3rd points
        ArrayList<String> styles = new ArrayList<>(1);
        String style = "";
        for (int i = 0; i < zones.length; i++) {
            if (!style.isEmpty()) {
                style += ", ";
            }
            style += zoneToString(i, 0, 0);
        }
        styles.add(style);
        return styles;
    }

    @Override
    public boolean dependsOnArrowIndex() {
        return true;
    }

    @Override
    public boolean is3DTarget() {
        return true;
    }
}
