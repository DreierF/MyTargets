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

package de.dreier.mytargets.utils;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import de.dreier.mytargets.ApplicationInstance;

public class HtmlInfoBuilder {
    private final StringBuilder info = new StringBuilder();

    public void addLine(int key, Object value) {
        if (info.length() != 0) {
            info.append("<br>");
        }
        info.append(getKeyValueLine(key, value));
    }

    public void addLine(String key, Object value) {
        if (info.length() != 0) {
            info.append("<br>");
        }
        info.append(getKeyValueLine(key, value));
    }

    @NonNull
    private String getKeyValueLine(String key, Object value) {
        return String.format("%s: <b>%s</b>", key, TextUtils.htmlEncode(value.toString()));
    }

    @NonNull
    private String getKeyValueLine(@StringRes int key, Object value) {
        return getKeyValueLine(ApplicationInstance.get(key), value);
    }

    @Override
    public String toString() {
        return info.toString();
    }
}
