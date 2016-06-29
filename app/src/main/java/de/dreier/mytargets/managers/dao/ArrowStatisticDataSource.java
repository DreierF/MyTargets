/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.models.ArrowStatistic;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.Target;

public class ArrowStatisticDataSource extends DataSourceBase {

    public List<ArrowStatistic> getAll() {
        List<ArrowStatistic> list = new ArrayList<>();
        Cursor res = database
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
                shot.passe = res.getLong(9);
                statistic.addShot(shot);
                lastArrowId = arrowId;
                lastArrowNumber = arrowNumber;
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }
}
