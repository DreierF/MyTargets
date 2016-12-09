/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.models;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Shot;

@Parcel
public class ArrowStatistic implements Comparable<ArrowStatistic> {

    private static final int[] BG_COLORS = {0xFFF44336, 0xFFFF5722, 0xFFFF9800, 0xFFFFC107, 0xFFFFEB3B, 0xFFCDDC39, 0xFF8BC34A, 0xFF4CAF50};
    private static final int[] TEXT_COLORS = {0xFFFFFFFF, 0xFFFFFFFF, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002};
    public String arrowName;
    public int arrowNumber;
    public int count = 0;
    public float xSum = 0;
    public float ySum = 0;
    public float reachedPointsSum = 0;
    public float maxPointsSum = 0;
    public Target target;
    public ArrayList<Shot> shots = new ArrayList<>();

    public static List<ArrowStatistic> getAll(List<Long> roundIds) {
        List<ArrowStatistic> list = new ArrayList<>();
        final String ids = Stream.of(roundIds).map(id -> Long.toString(id)).collect(Collectors.joining(","));
        Cursor res = FlowManager.getWritableDatabase(AppDatabase.class)
                .rawQuery(
                        "SELECT t.arrow,s.arrow AS number, s.x, s.y, s.points, r.target, r.scoring_style, s.arrow_index, n.name, p._id " +
                                "FROM TRAINING t, ROUND r, PASSE p, SHOOT s, ROUND_TEMPLATE a, ARROW n " +
                                "WHERE p._id=s.passe " +
                                "AND r._id=p.round " +
                                "AND t._id=r.training " +
                                "AND s.arrow>0 " +
                                "AND a._id=r.template " +
                                "AND t.arrow=n._id " +
                                "AND p.exact=1 " +
                                "AND r._id IN (" + ids + ") " +
                                "ORDER BY t.arrow, s.arrow", null);
        if (res.moveToFirst()) {
            long lastArrowId = 0;
            long lastArrowNumber = 0;
            ArrowStatistic statistic = null;
            do {
                long arrowId = res.getLong(0);
                int arrowNumber = res.getInt(1);

                Target target = new Target(res.getInt(5), res.getInt(6));
                if (statistic == null || lastArrowId != arrowId || lastArrowNumber != arrowNumber) {
                    statistic = new ArrowStatistic();
                    statistic.arrowName = res.getString(8);
                    statistic.arrowNumber = arrowNumber;
                    statistic.target = target;
                    list.add(statistic);
                }
                Shot shot = new Shot(res.getInt(7));
                shot.zone = res.getInt(4);
                shot.x = res.getFloat(2);
                shot.y = res.getFloat(3);
                shot.endId = res.getLong(9);
                statistic.addShot(shot);
                lastArrowId = arrowId;
                lastArrowNumber = arrowNumber;
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public float avgX() {
        return xSum / count;
    }

    public float avgY() {
        return ySum / count;
    }

    public float avgPoints() {
        return reachedPointsSum / count;
    }

    public int getAppropriateBgColor() {
        return BG_COLORS[((int) Math
                .ceil(reachedPointsSum * (BG_COLORS.length - 1) / maxPointsSum))];
    }

    public int getAppropriateTextColor() {
        return TEXT_COLORS[((int) Math
                .ceil(reachedPointsSum * (TEXT_COLORS.length - 1) / maxPointsSum))];
    }

    @Override
    public int compareTo(@NonNull ArrowStatistic another) {
        return Float.compare(another.avgPoints(), avgPoints());
    }

    public void addShot(Shot shot) {
        reachedPointsSum += target.getPointsByZone(shot.zone, shot.index);
        maxPointsSum += target.getMaxPoints();
        xSum += shot.x;
        ySum += shot.y;
        shots.add(shot);
        count++;
    }

    public void addShots(List<Shot> shots) {
        for (Shot shot : shots) {
            addShot(shot);
        }
    }
}
