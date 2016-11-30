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
