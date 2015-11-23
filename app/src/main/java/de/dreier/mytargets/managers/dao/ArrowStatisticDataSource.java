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
import java.util.List;

import de.dreier.mytargets.models.ArrowStatistic;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;

public class ArrowStatisticDataSource extends DataSourceBase {

    public ArrowStatisticDataSource(Context context) {
        super(context);
    }

    public List<ArrowStatistic> getAll() {
        List<ArrowStatistic> list = new ArrayList<>();
        ArrowDataSource arrowDataSource = new ArrowDataSource(getContext());
        Cursor res = database
                .rawQuery("SELECT t.arrow,s.arrow AS number, s.x, s.y, s.points, r.target, r.scoring_style, s.arrow_index " +
                        "FROM TRAINING t, ROUND r, PASSE p, SHOOT s, ROUND_TEMPLATE a " +
                        "WHERE p._id=s.passe " +
                        "AND r._id=p.round " +
                        "AND t._id=r.training " +
                        "AND s.arrow>0 " +
                        "AND a._id=r.template " +
                        "ORDER BY t.arrow, s.arrow", null);
        if (res.moveToFirst()) {
            long lastArrowId = 0, lastArrowNumber = 0;
            ArrowStatistic statistic = null;
            do {
                long arrowId = res.getLong(0);
                int arrowNumber = res.getInt(1);

                if (statistic == null || lastArrowId != arrowId || lastArrowNumber != arrowNumber) {
                    statistic = new ArrowStatistic();
                    statistic.arrow = arrowDataSource.get(arrowId);
                    statistic.arrowNumber = arrowNumber;
                    list.add(statistic);
                }
                Target target = TargetFactory.createTarget(context, res.getInt(5), res.getInt(6));
                statistic.pointsSum += target.getPointsByZone(res.getInt(4), res.getInt(7));
                statistic.xSum = res.getFloat(2);
                statistic.ySum = res.getFloat(3);
                statistic.count++;
                lastArrowId = arrowId;
                lastArrowNumber = arrowNumber;
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

}
