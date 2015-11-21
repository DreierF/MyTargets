/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;

import android.content.Context;
import android.database.Cursor;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;

public class StatisticsDataSource extends DataSourceBase {

    public StatisticsDataSource(Context context) {
        super(context);
    }

    public List<Entry> getAllTrainings() {
        Cursor res = database
                .rawQuery("SELECT s.points, r.target, s.passe, r.scoring_style, s.arrow_index  " +
                        "FROM TRAINING t, ROUND r, PASSE p, SHOOT s " +
                        "WHERE t._id=r.training AND r._id=p.round " +
                        "AND p._id=s.passe " +
                        "ORDER BY t.datum, t._id, r._id, p._id, s._id", null);
        res.moveToFirst();

        int oldPasse = -1;
        int actCounter = 0, maxCounter = 0;
        int passeIndex = 0;
        ArrayList<Entry> history = new ArrayList<>();
        for (int i = 0; i < res.getCount(); i++) {
            int zone = res.getInt(0);
            int passe = res.getInt(2);
            if (oldPasse != -1 && oldPasse != passe) {
                float percent = actCounter * 100.0f / (float) maxCounter;
                history.add(new Entry(percent, passeIndex++));
                actCounter = 0;
                maxCounter = 0;
            }
            Target target = TargetFactory.createTarget(context, res.getInt(1), res.getInt(3));
            actCounter += target.getPointsByZone(zone, res.getInt(4));
            maxCounter += target.getMaxPoints();
            oldPasse = passe;
            res.moveToNext();
        }
        float percent = actCounter * 100.0f / (float) maxCounter;
        history.add(new Entry(percent, passeIndex));
        res.close();
        return history;
    }

    public ArrayList<Entry> getAllRounds(long training) {
        Cursor res = database
                .rawQuery("SELECT s.points, r.target, s.passe, r.scoring_style, s.arrow_index " +
                        "FROM ROUND r, PASSE p, SHOOT s " +
                        "WHERE " + training + "=r.training AND r._id=p.round " +
                        "AND p._id=s.passe " +
                        "ORDER BY r._id, p._id, s._id", null);
        res.moveToFirst();

        int oldPasse = -1;
        int actCounter = 0, maxCounter = 0;
        int passeIndex = 0;
        ArrayList<Entry> history = new ArrayList<>();
        for (int i = 0; i < res.getCount(); i++) {
            int zone = res.getInt(0);
            int passe = res.getInt(2);
            if (oldPasse != -1 && oldPasse != passe) {
                float percent = actCounter * 100.0f / (float) maxCounter;
                history.add(new Entry(percent, passeIndex++));
                actCounter = 0;
                maxCounter = 0;
            }
            Target target = TargetFactory.createTarget(context, res.getInt(1), res.getInt(3));
            actCounter += target.getPointsByZone(zone, res.getInt(4));
            maxCounter += target.getMaxPoints();
            oldPasse = passe;
            res.moveToNext();
        }
        float percent = actCounter * 100.0f / (float) maxCounter;
        history.add(new Entry(percent, passeIndex));
        res.close();
        return history;
    }

}
