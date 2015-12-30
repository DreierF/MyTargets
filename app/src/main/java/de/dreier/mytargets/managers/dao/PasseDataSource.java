/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.utils.Pair;

public class PasseDataSource extends IdProviderDataSource<Passe> {
    public static final String TABLE = "PASSE";
    public static final String ROUND = "round";
    private static final String IMAGE = "image";
    private static final String EXACT = "exact";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ROUND + " INTEGER," +
                    IMAGE + " TEXT," +
                    EXACT + " INTEGER);";

    public PasseDataSource(Context context) {
        super(context, TABLE);
    }

    @Override
    public void update(Passe item) {
        super.update(item);
        ShotDataSource sds = new ShotDataSource(getContext());
        for (Shot shot : item.shot) {
            sds.update(shot);
        }
    }

    @Override
    public ContentValues getContentValues(Passe passe) {
        if (passe.shot.length == 0) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(ROUND, passe.roundId);
        values.put(EXACT, passe.exact ? 1 : 0);
        return values;
    }

    public Passe get(long round, int passe) {
        Cursor cursor = database
                .rawQuery("SELECT _id FROM PASSE WHERE round = " + round + " ORDER BY _id ASC",
                        null);
        if (!cursor.moveToPosition(passe)) {
            return null;
        }
        long passeId = cursor.getLong(0);
        cursor.close();
        Passe p = get(passeId);
        p.index = passe;
        return p;
    }

    private Passe get(long passeId) {
        Cursor cursor = database
                .rawQuery("SELECT exact FROM PASSE WHERE _id = " + passeId,
                        null);
        cursor.moveToFirst();
        boolean exact = cursor.getInt(0) != 0;
        cursor.close();
        Cursor res = database.rawQuery(
                "SELECT _id, passe, points, x, y, comment, arrow, arrow_index " +
                        "FROM SHOOT s " +
                        "WHERE s.passe = " + passeId + " " +
                        "ORDER BY _id ASC", null);
        int count = res.getCount();

        Passe p = new Passe(count);
        p.setId(passeId);
        p.exact = exact;
        p.index = -1;
        res.moveToFirst();
        for (int i = 0; i < count; i++) {
            p.shot[i] = ShotDataSource.cursorToShot(res, i);
            res.moveToNext();
        }
        res.close();
        return p;
    }

    public ArrayList<Passe> getAllByRound(long round) {
        Cursor res = database.rawQuery(
                "SELECT s._id, s.passe, s.points, s.x, s.y, s.comment, s.arrow, s.arrow_index, " +
                        "(SELECT COUNT(x._id) FROM SHOOT x WHERE x.passe=p._id), p.exact " +
                        "FROM PASSE p  " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE p.round = " + round + " " +
                        "ORDER BY p._id ASC, s._id ASC", null);
        ArrayList<Passe> list = new ArrayList<>();
        if (res.moveToFirst()) {
            long oldRoundId = -1;
            int pIndex = 0;
            do {
                int ppp = res.getInt(8);
                if (ppp == 0) {
                    res.moveToNext();
                    continue;
                }
                Passe passe = new Passe(ppp);
                passe.setId(res.getLong(1));
                passe.exact = res.getInt(9) != 0;
                passe.roundId = round;
                if (oldRoundId != passe.roundId) {
                    pIndex = 0;
                    oldRoundId = passe.roundId;
                }
                passe.index = pIndex++;
                for (int i = 0; i < ppp; i++) {
                    passe.shot[i] = ShotDataSource.cursorToShot(res, i);
                    res.moveToNext();
                }
                list.add(passe);
            } while (!res.isAfterLast());
        }
        res.close();
        return list;
    }

    public ArrayList<Passe> getAllByTraining(long training) {
        Cursor res = database.rawQuery(
                "SELECT s._id, s.passe, s.points, s.x, s.y, s.comment, s.arrow, s.arrow_index, r._id, " +
                        "(SELECT COUNT(x._id) FROM SHOOT x WHERE x.passe=p._id), p.exact " +
                        "FROM ROUND r " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE r.training = " + training + " " +
                        "ORDER BY r._id ASC, p._id ASC, s._id ASC", null);
        ArrayList<Passe> list = new ArrayList<>();
        if (res.moveToFirst()) {
            long oldRoundId = -1;
            int pIndex = 0;
            do {
                int ppp = res.getInt(9);
                if (ppp == 0) {
                    res.moveToNext();
                    continue;
                }
                Passe passe = new Passe(ppp);
                passe.setId(res.getLong(1));
                passe.roundId = res.getLong(8);
                passe.exact = res.getInt(9) != 0;
                if (oldRoundId != passe.roundId) {
                    pIndex = 0;
                    oldRoundId = passe.roundId;
                }
                passe.index = pIndex++;
                for (int i = 0; i < ppp; i++) {
                    passe.shot[i] = ShotDataSource.cursorToShot(res, i);
                    res.moveToNext();
                }
                list.add(passe);
            } while (!res.isAfterLast());
        }
        res.close();
        return list;
    }

    public float getAverageScore(long training) {
        Cursor res = database.rawQuery(
                "SELECT s.points, a.target, a.scoring_style, s.arrow_index " +
                        "FROM ROUND r " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "LEFT JOIN ROUND_TEMPLATE a ON a._id = r.template " +
                        "WHERE r.training = " + training + " " +
                        "ORDER BY r._id ASC, p._id ASC, s._id ASC", null);
        int count = 0;
        int sum = 0;
        if (res.moveToFirst()) {
            do {
                count++;
                sum += TargetFactory.createTarget(context, res.getInt(1), res.getInt(2)).getPointsByZone(res.getInt(0), res.getInt(3));
            } while (res.moveToNext());
        }
        res.close();
        float average = 0;
        if (count > 0) {
            average = ((sum * 100) / count) / 100.0f;
        }
        return average;
    }

    private Map<Pair<Integer, String>, Integer> getScoreDistribution(long training) {
        Cursor cursor = database
                .rawQuery("SELECT a.target, a.scoring_style, s.points, s.arrow_index, COUNT(*) " +
                        "FROM ROUND r " +
                        "LEFT JOIN ROUND_TEMPLATE a ON r.template=a._id " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE r.training=" + training + " " +
                        "GROUP BY a.target, a.scoring_style, s.points, s.arrow_index", null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            throw new IllegalStateException("There must be at least one round!");
        }
        Target t = TargetFactory.createTarget(context, cursor.getInt(0), cursor.getInt(1));
        Map<Pair<Integer, String>, Integer> scoreCount = new HashMap<>();
        for (int arrow = 0; arrow < 3; arrow++) {
            for (int zone = -1; zone < t.getZones(); zone++) {
                scoreCount.put(new Pair<>(t.getPointsByZone(zone, arrow),
                        t.zoneToString(zone, arrow)), 0);
            }
            if (!t.dependsOnArrowIndex()) {
                break;
            }
        }
        do {
            if (cursor.isNull(2)) {
                continue;
            }
            int zone = cursor.getInt(2);
            int arrow = cursor.getInt(3);
            int count = cursor.getInt(4);
            Pair<Integer, String> tuple = new Pair<>(t.getPointsByZone(zone, arrow),
                    t.zoneToString(zone, arrow));
            count += scoreCount.get(tuple);
            scoreCount.put(tuple, count);
        } while (cursor.moveToNext());
        cursor.close();
        return scoreCount;
    }

    public ArrayList<Pair<String, Integer>> getTopScoreDistribution(long training) {
        Map<Pair<Integer, String>, Integer> scoreCount = getScoreDistribution(training);
        ArrayList<Pair<Integer, String>> list = new ArrayList<>(scoreCount.keySet());
        Collections.sort(list, (lhs, rhs) -> {
            if (lhs.getFirst().equals(rhs.getFirst())) {
                return -lhs.getSecond().compareTo(rhs.getSecond());
            }
            if (lhs.getFirst() > rhs.getFirst()) {
                return -1;
            }
            return 1;
        });
        ArrayList<Pair<String, Integer>> topScore = new ArrayList<>();
        topScore.add(new Pair<>(list.get(0).getSecond(), scoreCount.get(list.get(0))));
        boolean collapseFirst = list.get(0).getFirst().equals(list.get(1).getFirst());
        if (list.size() == 1) {
            return topScore;
        }
        if (collapseFirst) {
            topScore.add(new Pair<>(list.get(1).getSecond() + "+" + list.get(0).getSecond(),
                    scoreCount.get(list.get(1)) + scoreCount.get(list.get(0))));
        } else {
            topScore.add(new Pair<>(list.get(1).getSecond(), scoreCount.get(list.get(1))));
        }
        if (list.size() == 2) {
            return topScore;
        }
        topScore.add(new Pair<>(list.get(2).getSecond(), scoreCount.get(list.get(2))));
        return topScore;
    }
}
