/*
 * Copyright (C) 2017 Florian Dreier
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
package de.dreier.mytargets.shared.migration;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.Training;

public class TrainingDataSource extends IdProviderDataSource<Training> {
    private static final String TABLE = "TRAINING";
    private static final String TITLE = "title";
    private static final String DATE = "datum";
    private static final String ARROW = "arrow";
    private static final String BOW = "bow";
    private static final String STANDARD_ROUND = "standard_round";
    private static final String ARROW_NUMBERING = "arrow_numbering";
    private static final String TIME_PER_PASSE = "time";
    private static final String WEATHER = "weather";
    private static final String WIND_SPEED = "wind_speed";
    private static final String WIND_DIRECTION = "wind_direction";
    private static final String LOCATION = "location";
    private static final String EXACT = "exact";

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DATE + " INTEGER," +
                    TITLE + " TEXT," +
                    WEATHER + " INTEGER," +
                    WIND_SPEED + " INTEGER," +
                    WIND_DIRECTION + " INTEGER," +
                    LOCATION + " TEXT," +
                    STANDARD_ROUND + " INTEGER," +
                    BOW + " INTEGER," +
                    ARROW + " INTEGER," +
                    ARROW_NUMBERING + " INTEGER," +
                    TIME_PER_PASSE + " INTEGER," +
                    EXACT + " INTEGER);";

    public TrainingDataSource(Context context) {
        super(context, TABLE);
    }

    @Override
    public ContentValues getContentValues(Training training) {
        ContentValues values = new ContentValues();
        values.put(TITLE, training.title);
        values.put(DATE, training.date.getTime());
        values.put(STANDARD_ROUND, training.standardRoundId);
        values.put(BOW, training.bow);
        values.put(ARROW, training.arrow);
        values.put(ARROW_NUMBERING, training.arrowNumbering ? 1 : 0);
        values.put(TIME_PER_PASSE, training.timePerPasse);
        values.put(WEATHER, training.environment.weather.getValue());
        values.put(WIND_SPEED, training.environment.windSpeed);
        values.put(WIND_DIRECTION, training.environment.windDirection);
        values.put(LOCATION, training.environment.location);
        values.put(EXACT, training.exact ? 1 : 0);
        return values;
    }

    private Training cursorToTraining(Cursor cursor, int startColumnIndex) {
        Training training = new Training();
        training.environment = new Environment();
        training.setId(cursor.getLong(startColumnIndex));
        training.title = cursor.getString(startColumnIndex + 1);
        training.date = new Date(cursor.getLong(startColumnIndex + 2));
        training.bow = cursor.getInt(startColumnIndex + 3);
        training.arrow = cursor.getInt(startColumnIndex + 4);
        training.standardRoundId = cursor.getLong(startColumnIndex + 5);
        training.arrowNumbering = cursor.getInt(startColumnIndex + 6) == 1;
        training.timePerPasse = cursor.getInt(startColumnIndex + 7);
        training.environment.weather = EWeather.getOfValue(cursor.getInt(startColumnIndex + 8));
        training.environment.windSpeed = cursor.getInt(startColumnIndex + 9);
        training.environment.windDirection = cursor.getInt(startColumnIndex + 10);
        training.environment.location = cursor.getString(startColumnIndex + 11);
        training.exact = cursor.getInt(startColumnIndex + 12) == 1;
        return training;
    }

    public Training get(long training) {
        Cursor cursor = database.rawQuery(
                "SELECT t._id, t.title, t.datum, t.bow, t.arrow, t.standard_round, " +
                        "t.arrow_numbering, t.time, " +
                        "t.weather, t.wind_speed, t.wind_direction, t.location, t.exact " +
                        "FROM TRAINING t " +
                        "LEFT JOIN ROUND r ON t._id = r.training " +
                        "LEFT JOIN ROUND_TEMPLATE a ON r.template=a._id " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE t._id = " + training, null);

        Training tr = null;
        if (cursor.moveToFirst()) {
            tr = cursorToTraining(cursor, 0);
        }
        cursor.close();
        return tr;
    }

    public ArrayList<Training> getAll() {
        Cursor cursor = database.rawQuery(
                "SELECT t._id, t.title, t.datum, t.bow, t.arrow, t.standard_round, " +
                        "t.arrow_numbering, t.time, " +
                        "t.weather, t.wind_speed, t.wind_direction, t.location, t.exact " +
                        "FROM TRAINING t " +
                        "LEFT JOIN ROUND r ON t._id = r.training " +
                        "LEFT JOIN ROUND_TEMPLATE a ON r.template=a._id " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "LEFT JOIN BOW b ON b._id = t.bow " +
                        "GROUP BY t._id " +
                        "ORDER BY t.datum DESC", null);

        ArrayList<Training> list = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToTraining(cursor, 0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
