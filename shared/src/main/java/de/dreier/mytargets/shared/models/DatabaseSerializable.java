/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.database.Cursor;

public interface DatabaseSerializable {
    long getId();
    void setId(long id);
    String getTableName();
    ContentValues getContentValues();
    void fromCursor(Cursor cursor);
}
