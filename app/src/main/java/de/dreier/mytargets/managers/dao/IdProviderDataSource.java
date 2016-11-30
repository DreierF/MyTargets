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
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.VisibleForTesting;

import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.IIdSettable;

public abstract class IdProviderDataSource<T extends IIdSettable> extends DataSourceBase {
    public static final String ID = "_id";

    // Database fields
    private final String table;

    IdProviderDataSource(String table) {
        this.table = table;
    }

    IdProviderDataSource(Context context, String table, DatabaseManager dbHelper, SQLiteDatabase database) {
        super(context, dbHelper, database);
        this.table = table;
    }

    public void update(T item) {
        ContentValues values = getContentValues(item);
        if (values == null) {
            return;
        }
        if (item.getId() <= 0) {
            item.setId(database.insert(table, null, values));
        } else {
            values.put(ID, item.getId());
            database.replace(table, null, values);
        }
    }

    public void delete(T item) {
        delete(item.getId());
    }

    public void delete(long itemId) {
        database.delete(table, ID + "=" + itemId, null);
        DatabaseManager.cleanup(database);
    }

    protected abstract ContentValues getContentValues(T item);

    @VisibleForTesting
    public void deleteAll() {
        database.delete(table, null, null);
    }
}
