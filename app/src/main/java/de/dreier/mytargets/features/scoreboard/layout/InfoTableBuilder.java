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

package de.dreier.mytargets.features.scoreboard.layout;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.features.scoreboard.builder.model.Table;

public class InfoTableBuilder {
    public final Table info = new Table(true);

    public void addLine(int key, @NonNull Object value) {
        getKeyValueLine(info.startRow(), key, value);
    }

    public void addLine(String key, @NonNull Object value) {
        getKeyValueLine(info.startRow(), key, value);
    }

    @NonNull
    private void getKeyValueLine(Table.Row row, String key, @NonNull Object value) {
        row.addCell(key).addBoldCell(value.toString());
    }

    @NonNull
    private void getKeyValueLine(Table.Row row, @StringRes int key, @NonNull Object value) {
        getKeyValueLine(row, ApplicationInstance.get(key), value);
    }

    @NonNull
    @Override
    public String toString() {
        return info.toString();
    }
}
