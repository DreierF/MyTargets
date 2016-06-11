/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.managers.DatabaseManager;

public abstract class DataSourceBase {
    private final Context context;

    // Database fields
    final SQLiteDatabase database;
    final DatabaseManager dbHelper;

    DataSourceBase() {
        this.context = ApplicationInstance.getContext();
        this.dbHelper = DatabaseManager.getInstance(context);
        this.database = dbHelper.getWritableDatabase();
    }

    DataSourceBase(Context context, DatabaseManager dbHelper, SQLiteDatabase database) {
        this.dbHelper = dbHelper;
        this.context = context;
        this.database = database;
    }

    Context getContext() {
        return context;
    }
}
