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
package de.dreier.mytargets.managers.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.utils.Pair;

public class PasseDataSource extends IdProviderDataSource<Passe> {
    private static final String TABLE = "PASSE";
    private static final String ROUND = "round";
    private static final String IMAGE = "image";
    private static final String EXACT = "exact";
    private static final String SAVE_TIME = "save_time";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ROUND + " INTEGER," +
                    IMAGE + " TEXT," +
                    EXACT + " INTEGER," +
                    SAVE_TIME + " INTEGER);";

    public PasseDataSource() {
        super(TABLE);
    }

    @Override
    public void update(Passe item) {
        super.update(item);
        ShotDataSource sds = new ShotDataSource();
        for (Shot shot : item.shots) {
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
        if (passe.shots.size() == 0) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(ROUND, passe.roundId);
        values.put(EXACT, passe.exact ? 1 : 0);
        values.put(SAVE_TIME, passe.saveDate.toDate().getTime());
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
                "SELECT s._id, s.passe, s.points, s.x, s.y, s.comment, s.arrow, s.arrow_index, p.exact, p.save_time, p.round " +
                        "FROM SHOOT s, PASSE p " +
                        "WHERE s.passe=p._id " +
                        "AND p._id=" + passeId + " " +
                        "ORDER BY s.arrow_index ASC", null);
        int count = res.getCount();

        res.moveToFirst();
        Passe p = new Passe(count);
        p.setId(passeId);
        p.index = -1;
        p.exact = res.getInt(8) == 1;
        p.saveDate = new DateTime(res.getLong(9));
        p.roundId = res.getLong(10);
        for (int i = 0; i < count; i++) {
            p.shots.set(i, ShotDataSource.cursorToShot(res, i));
            res.moveToNext();
        }
        res.close();
        return p;
    }

    public List<Passe> getAllByRound(long round) {
        Cursor res = database.rawQuery(
                "SELECT s._id, s.passe, s.points, s.x, s.y, s.comment, s.arrow, s.arrow_index, " +
                        "(SELECT COUNT(x._id) FROM SHOOT x WHERE x.passe=p._id), p.exact, p.save_time " +
                        "FROM PASSE p  " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE p.round = " + round + " " +
                        "ORDER BY p._id ASC, s.arrow_index ASC", null);
        List<Passe> list = new ArrayList<>();
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
                passe.saveDate = new DateTime(res.getLong(10));
                if (oldRoundId != passe.roundId) {
                    pIndex = 0;
                    oldRoundId = passe.roundId;
                }
                passe.index = pIndex++;
                for (int i = 0; i < ppp; i++) {
                    passe.shots.set(i, ShotDataSource.cursorToShot(res, i));
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
                        "(SELECT COUNT(x._id) FROM SHOOT x WHERE x.passe=p._id), p.exact, p.save_time " +
                        "FROM ROUND r " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE r.training = " + training + " " +
                        "ORDER BY r._id ASC, p._id ASC, s.arrow_index ASC", null);
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
                passe.exact = res.getInt(10) == 1;
                passe.saveDate = new DateTime(res.getLong(11));
                if (oldRoundId != passe.roundId) {
                    pIndex = 0;
                    oldRoundId = passe.roundId;
                }
                passe.index = pIndex++;
                for (int i = 0; i < ppp; i++) {
                    passe.shots.set(i, ShotDataSource.cursorToShot(res, i));
                    res.moveToNext();
                }
                list.add(passe);
            } while (!res.isAfterLast());
        }
        res.close();
        return list;
    }

    @NonNull
    private Map<SelectableZone, Integer> getRoundScores(List<Round> rounds) {
        final Target t = rounds.get(0).info.target;
        final Set<SelectableZone> selectableZones = t.getAllPossibleSelectableZones();
        Map<SelectableZone, Integer> scoreCount = new HashMap<>();
        for (SelectableZone zone : selectableZones) {
            scoreCount.put(zone, 0);
        }
        for (Round round : rounds) {
            List<Passe> passes = new PasseDataSource().getAllByRound(round.getId());
            for (Passe p : passes) {
                for (Shot s : p.shots) {
                    SelectableZone tuple = new SelectableZone(s.zone, t.getModel().getZone(s.zone),
                            t.zoneToString(s.zone, s.index), t.getPointsByZone(s.zone, s.index));
                    final Integer integer = scoreCount.get(tuple);
                    if (integer != null) {
                        int count = integer + 1;
                        scoreCount.put(tuple, count);
                    }
                }
            }
        }
        return scoreCount;
    }

    public List<Pair<String, Integer>> getTopScoreDistribution(List<Map.Entry<SelectableZone, Integer>> sortedScore) {
        final List<Pair<String, Integer>> result = Stream.of(sortedScore)
                .map(value -> new Pair<>(value.getKey().text, value.getValue()))
                .collect(Collectors.toList());

        // Collapse first two entries if they yield the same score points,
        // e.g. 10 and X => {X, 10+X, 9, ...}
        if (sortedScore.size() > 1) {
            Map.Entry<SelectableZone, Integer> first = sortedScore.get(0);
            Map.Entry<SelectableZone, Integer> second = sortedScore.get(1);
            if (first.getKey().points == second.getKey().points) {
                final String newTitle = second.getKey().text + "+" + first.getKey().text;
                result.set(1, new Pair<>(newTitle, second.getValue() + first.getValue()));
            }
        }
        return result;
    }

    /**
     * Compound 9ers are already collapsed to one SelectableZone.
     */
    public List<Map.Entry<SelectableZone, Integer>> getSortedScoreDistribution(List<Round> rounds) {
        Map<SelectableZone, Integer> scoreCount = getRoundScores(rounds);
        return Stream.of(scoreCount)
                .sorted((lhs, rhs) -> lhs.getKey().compareTo(rhs.getKey()))
                .collect(Collectors.toList());
    }
}
