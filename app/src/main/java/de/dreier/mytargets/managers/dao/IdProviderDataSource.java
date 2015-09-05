/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;

import android.content.ContentValues;
import android.content.Context;

import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.IdProvider;

public abstract class IdProviderDataSource<T extends IdProvider> extends DataSourceBase<T> {
    public static final String ID = "_id";

    // Database fields
    private String TABLE;

    public IdProviderDataSource(Context context, String table) {
        super(context);
        TABLE = table;
    }

    public void update(T item) {
        ContentValues values = getContentValues(item);
        if (values == null) {
            return;
        }
        if (item.getId() <= 0) {
            item.setId(database.insert(TABLE, null, values));
        } else {
            values.put(ID, item.getId());
            database.replace(TABLE, null, values);
        }
    }

    public void delete(T item) {
        database.delete(TABLE, ID + "=" + item.getId(), null);
        DatabaseManager.cleanup(database);
    }

    protected abstract ContentValues getContentValues(T item);
}
