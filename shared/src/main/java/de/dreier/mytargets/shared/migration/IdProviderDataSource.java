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
import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

public abstract class IdProviderDataSource<T extends IIdSettableOld> extends DataSourceBase {
    public static final String ID = "_id";
    private final String table;

    IdProviderDataSource(Context context, String table, DatabaseWrapper database) {
        super(context, database);
        this.table = table;
    }

    public void update(T item) {
        ContentValues values = getContentValues(item);
        if (values == null) {
            return;
        }
        if (item.getId() <= 0) {
            item.setId(database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_NONE));
        } else {
            values.put(ID, item.getId());
            database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    protected abstract ContentValues getContentValues(T item);
}
