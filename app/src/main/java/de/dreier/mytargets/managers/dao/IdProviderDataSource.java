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
    private String table;

    public IdProviderDataSource(Context context, String table) {
        super(context);
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
        database.delete(table, ID + "=" + item.getId(), null);
        DatabaseManager.cleanup(database);
    }

    protected abstract ContentValues getContentValues(T item);
}
