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

public abstract class DataSourceBase {
    protected Context context;

    // Database fields
    protected SQLiteDatabase database;
    protected DatabaseManager dbHelper;

    public DataSourceBase(Context context) {
        this.dbHelper = DatabaseManager.getInstance(context);
        this.context = context;
        this.database = dbHelper.getWritableDatabase();
    }

    public DataSourceBase(Context context, DatabaseManager dbHelper, SQLiteDatabase database) {
        this.dbHelper = dbHelper;
        this.context = context;
        this.database = database;
    }

    protected Context getContext() {
        return context;
    }
}
