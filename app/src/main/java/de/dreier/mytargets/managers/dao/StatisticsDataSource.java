/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;

import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.utils.Pair;

public class StatisticsDataSource extends DataSourceBase {

    public List<Pair<Integer, Integer>> getAllTrainings() {
        Cursor res = database
                .rawQuery("SELECT s.points, r.target, s.passe, r.scoring_style, s.arrow_index  " +
                        "FROM TRAINING t, ROUND r, PASSE p, SHOOT s " +
                        "WHERE t._id=r.training AND r._id=p.round " +
                        "AND p._id=s.passe " +
                        "ORDER BY t.datum, t._id, r._id, p._id, s._id", null);

        return getLineDataFromPasseCursor(res);
    }

    @NonNull
    private List<Pair<Integer, Integer>> getLineDataFromPasseCursor(Cursor res) {
        res.moveToFirst();

        int oldPasse = -1;
        int actCounter = 0;
        int maxCounter = 0;
        int passeIndex = 0;
        List<Pair<Integer, Integer>> history = new ArrayList<>();
        for (int i = 0; i < res.getCount(); i++) {
            int zone = res.getInt(0);
            int passe = res.getInt(2);
            if (oldPasse != -1 && oldPasse != passe) {
                float percent = actCounter * 100.0f / (float) maxCounter;
                history.add(new Pair<>((int)percent, passeIndex++));
                actCounter = 0;
                maxCounter = 0;
            }
            Target target = new Target(res.getInt(1), res.getInt(3));
            actCounter += target.getPointsByZone(zone, res.getInt(4));
            maxCounter += target.getMaxPoints();
            oldPasse = passe;
            res.moveToNext();
        }
        float percent = actCounter * 100.0f / (float) maxCounter;
        history.add(new Pair<>((int)percent, passeIndex));
        res.close();

        return history;
    }

    public List<Pair<Integer, Integer>> getAllRounds(long training) {
        Cursor res = database
                .rawQuery("SELECT s.points, r.target, s.passe, r.scoring_style, s.arrow_index " +
                        "FROM ROUND r, PASSE p, SHOOT s " +
                        "WHERE " + training + "=r.training AND r._id=p.round " +
                        "AND p._id=s.passe " +
                        "ORDER BY r._id, p._id, s._id", null);
        return getLineDataFromPasseCursor(res);
    }

}
