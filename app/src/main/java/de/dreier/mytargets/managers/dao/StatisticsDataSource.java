/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;

public class StatisticsDataSource extends DataSourceBase {

    public StatisticsDataSource(Context context) {
        super(context);
    }

    public LineData getAllTrainings() {
        Cursor res = database
                .rawQuery("SELECT s.points, r.target, s.passe, r.scoring_style, s.arrow_index  " +
                        "FROM TRAINING t, ROUND r, PASSE p, SHOOT s " +
                        "WHERE t._id=r.training AND r._id=p.round " +
                        "AND p._id=s.passe " +
                        "ORDER BY t.datum, t._id, r._id, p._id, s._id", null);

        return getLineDataFromPasseCursor(res);
    }

    @NonNull
    private LineData getLineDataFromPasseCursor(Cursor res) {
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


        LineDataSet series = new LineDataSet(history, "All trainings");
        series.setColors(new int[]{0xFF33B5E5});
        series.setLineWidth(2);

        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < series.getEntryCount(); i++) {
            xValues.add(String.valueOf(i));
        }
        LineData data = new LineData(xValues, series);
        data.setDrawValues(false);
        return data;
    }

    public LineData getAllRounds(long training) {
        Cursor res = database
                .rawQuery("SELECT s.points, r.target, s.passe, r.scoring_style, s.arrow_index " +
                        "FROM ROUND r, PASSE p, SHOOT s " +
                        "WHERE " + training + "=r.training AND r._id=p.round " +
                        "AND p._id=s.passe " +
                        "ORDER BY r._id, p._id, s._id", null);
        return getLineDataFromPasseCursor(res);
    }

}
