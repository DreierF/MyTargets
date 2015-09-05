/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import de.dreier.mytargets.managers.DatabaseManager;

public abstract class DataSourceBase<T> {
    private final Context context;

    // Database fields
    protected SQLiteDatabase database;
    private DatabaseManager dbHelper;

    public DataSourceBase(Context context) {
        dbHelper = DatabaseManager.getInstance(context);
        this.context = context;
        this.database = dbHelper.getWritableDatabase();
    }

    protected Context getContext() {
        return context;
    }

    public void close() {
        dbHelper.close();
    }
}
