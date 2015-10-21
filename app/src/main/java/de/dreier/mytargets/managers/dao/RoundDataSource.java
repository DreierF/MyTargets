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
import android.view.View;

import java.util.ArrayList;

import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;

import static de.dreier.mytargets.managers.dao.RoundTemplateDataSource.SCORING_STYLE;
import static de.dreier.mytargets.managers.dao.RoundTemplateDataSource.TARGET;


public class RoundDataSource extends IdProviderDataSource<Round> {
    public static final String TABLE = "ROUND";
    public static final String TRAINING = "training";
    public static final String COMMENT = "comment";
    public static final String TEMPLATE = "template";

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TRAINING + " INTEGER," +
                    COMMENT + " TEXT," +
                    TEMPLATE + " INTEGER," +
                    TARGET + " INTEGER," +
                    SCORING_STYLE + " INTEGER);";

    public RoundDataSource(Context context) {
        super(context, TABLE);
    }

    @Override
    public ContentValues getContentValues(Round round) {
        ContentValues values = new ContentValues();
        values.put(COMMENT, round.comment);
        values.put(TRAINING, round.training);
        values.put(TEMPLATE, round.info.getId());
        values.put(TARGET, round.info.target.getId());
        values.put(SCORING_STYLE, round.info.target.scoringStyle);
        return values;
    }

    private Round cursorToRound(Cursor cursor, int startColumnIndex) {
        Round round = new Round();
        round.setId(cursor.getLong(startColumnIndex));
        round.training = cursor.getLong(startColumnIndex + 1);
        round.comment = cursor.getString(startColumnIndex + 2);
        if (round.comment == null) {
            round.comment = "";
        }
        round.info = RoundTemplateDataSource.cursorToRoundTemplate(cursor, getContext(), 3);
        return round;
    }

    public Round get(long round) {
        Cursor cursor = database.rawQuery(
                "SELECT r._id, r.training, r.comment, " +
                        "a._id, a.r_index, a.arrows, a.target, a.scoring_style, " +
                        "r.target, r.scoring_style, a.distance, a.unit, " +
                        "a.size, a.target_unit, a.passes, a.sid " +
                        "FROM ROUND r " +
                        "LEFT JOIN ROUND_TEMPLATE a ON r.template=a._id " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE r._id=" + round, null);

        if (!cursor.moveToFirst()) {
            return null;
        }
        Round r = cursorToRound(cursor, 0);
        cursor.close();

        // Calculate reached points
        Cursor cur = database.rawQuery("SELECT s.points, s.arrow_index " +
                "FROM PASSE p, SHOOT s " +
                "WHERE p.round=" + round + " " +
                "AND s.passe=p._id", null);
        if (cur.moveToFirst()) {
            do {
                r.reachedPoints += r.info.target.getPointsByZone(cur.getInt(0), cur.getInt(1));
            } while (cur.moveToNext());
        }
        cur.close();
        return r;
    }

    public ArrayList<Round> getAll(long training) {
        Cursor res = database.rawQuery("SELECT r._id " +
                "FROM ROUND r " +
                "WHERE r.training=" + training + " " +
                "ORDER BY r._id ASC", null);
        ArrayList<Round> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            do {
                Round r = get(res.getLong(0));
                list.add(r);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    @Override
    public void delete(Round item) {
        // Get list of RoundTemplates for the current standard round
        long standardRoundId = item.info.standardRound;
        StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource(context);
        StandardRound standardRound = standardRoundDataSource.get(standardRoundId);
        ArrayList<RoundTemplate> roundTemplates = standardRound.getRounds();

        if (standardRound.club != StandardRound.CUSTOM_PRACTICE) {
            throw new IllegalStateException("Only practice rounds may be deleted!");
        }
        if (standardRound.getRounds().size() < 1) {
            throw new IllegalStateException("There must be at least one round!");
        }

        // Remove round
        super.delete(item);

        // Remove current round template
        RoundTemplate removedTemplate = roundTemplates.get(item.info.index);
        RoundTemplateDataSource roundTemplateDataSource = new RoundTemplateDataSource(getContext());
        roundTemplateDataSource.delete(removedTemplate);
        roundTemplates.remove(removedTemplate);

        // Update templates indices
        for (RoundTemplate roundTemplate : roundTemplates) {
            if (roundTemplate.index > item.info.index) {
                roundTemplate.index--;
            }
        }
        standardRound.setRounds(roundTemplates);
        standardRoundDataSource.update(standardRound);
    }
}
