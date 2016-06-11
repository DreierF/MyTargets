/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.utils.Pair;

public class PasseDataSource extends IdProviderDataSource<Passe> {
    private static final String TABLE = "PASSE";
    private static final String ROUND = "round";
    private static final String IMAGE = "image";
    private static final String EXACT = "exact";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ROUND + " INTEGER," +
                    IMAGE + " TEXT," +
                    EXACT + " INTEGER);";

    public PasseDataSource() {
        super(TABLE);
    }

    @Override
    public void update(Passe item) {
        super.update(item);
        ShotDataSource sds = new ShotDataSource();
        for (Shot shot : item.shot) {
            sds.update(shot);
        }
    }

    @Override
    public void delete(Passe item) {
        super.delete(item);
        Round r = new RoundDataSource().get(item.roundId);
        new RoundTemplateDataSource().deletePasse(r.info);
    }

    @Override
    public ContentValues getContentValues(Passe passe) {
        if (passe.shot.length == 0) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(ROUND, passe.roundId);
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
        Cursor res = database.rawQuery(
                "SELECT s._id, s.passe, s.points, s.x, s.y, s.comment, s.arrow, s.arrow_index, p.exact " +
                        "FROM SHOOT s, PASSE p " +
                        "WHERE s.passe=p._id " +
                        "AND p._id=" + passeId + " " +
                        "ORDER BY s._id ASC", null);
        int count = res.getCount();

        res.moveToFirst();
        Passe p = new Passe(count);
        p.setId(passeId);
        p.index = -1;
        p.exact = res.getInt(8) == 1;
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
                passe.roundId = round;
                passe.exact = res.getInt(9) == 1;
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
                sum += new Target(res.getInt(1), res.getInt(2))
                        .getPointsByZone(res.getInt(0), res.getInt(3));
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
        Target t = new Target(cursor.getInt(0), cursor.getInt(1));
        Map<Pair<Integer, String>, Integer> scoreCount = new HashMap<>();
        for (int arrow = 0; arrow < 3; arrow++) {
            for (int zone = -1; zone < t.getModel().getZoneCount(); zone++) {
                scoreCount.put(new Pair<>(t.getPointsByZone(zone, arrow),
                        t.zoneToString(zone, arrow)), 0);
            }
            if (!t.getModel().dependsOnArrowIndex()) {
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

    public List<Pair<String, Integer>> getTopScoreDistribution(long training) {
        Map<Pair<Integer, String>, Integer> scoreCount = getScoreDistribution(training);
        List<Map.Entry<Pair<Integer, String>, Integer>> sortedScore = Stream.of(scoreCount)
                .sorted((lhs, rhs) -> {
                    if (lhs.getKey().getFirst().equals(rhs.getKey().getFirst())) {
                        return -lhs.getKey().getSecond().compareTo(rhs.getKey().getSecond());
                    }
                    return rhs.getKey().getFirst() - lhs.getKey().getFirst();
                })
                .collect(Collectors.toList());

        // Collapse first two entries if they yield the same score points,
        // e.g. 10 and X => {X, 10+X, 9, ...}
        if (sortedScore.size() > 1) {
            Map.Entry<Pair<Integer, String>, Integer> first = sortedScore.get(0);
            Map.Entry<Pair<Integer, String>, Integer> second = sortedScore.get(1);
            if (first.getKey().getFirst().equals(second.getKey().getFirst())) {
                final String newTitle = second.getKey().getSecond() + "+" + first.getKey()
                        .getSecond();
                final int newValue = second.getValue() + first.getValue();
                sortedScore.get(1).getKey().setSecond(newTitle);
                sortedScore.get(1).setValue(newValue);
                second.setValue(newValue);
            }
        }
        return Stream.of(sortedScore)
                .map(value -> new Pair<>(value.getKey().getSecond(), value.getValue()))
                .collect(Collectors.toList());
    }
}
