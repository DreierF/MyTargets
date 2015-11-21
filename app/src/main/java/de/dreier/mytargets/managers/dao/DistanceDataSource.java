/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;


import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import de.dreier.mytargets.shared.models.Distance;

public class DistanceDataSource extends DataSourceBase {

    public DistanceDataSource(Context context) {
        super(context);
    }

    /**
     * Returns a list of all distances that are either default values or used somewhere in the app
     *
     * @param distance Distance id to add to the list, because it is the current value.
     * @param unit     Distances are only returned which match the specified unit
     *                 (Distance.METER or Distance.YARDS)
     * @return List of distances
     */
    public List<Distance> getAll(Distance distance, String unit) {
        ArrayList<Distance> distances = new ArrayList<>();
        HashSet<Long> set = new HashSet<>();

        // Add currently selected distance to list
        if (distance.unit.equals(unit)) {
            distances.add(distance);
            set.add(distance.getId());
        }

        // Get all distances used in ROUND or VISIER table
        Cursor cur = database.rawQuery(
                "SELECT * FROM (SELECT DISTINCT distance, unit FROM ROUND_TEMPLATE UNION SELECT DISTINCT distance, unit FROM VISIER) WHERE unit=?",
                new String[]{unit});
        if (cur.moveToFirst()) {
            do {
                Distance d = new Distance(cur.getInt(0), cur.getString(1));
                if (!set.contains(d.getId())) {
                    distances.add(d);
                    set.add(d.getId());
                }
            } while (cur.moveToNext());
        }
        cur.close();
        Collections.sort(distances);
        return distances;
    }
}
