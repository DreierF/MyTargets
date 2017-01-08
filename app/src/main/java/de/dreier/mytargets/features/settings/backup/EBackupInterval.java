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

package de.dreier.mytargets.features.settings.backup;

import android.support.annotation.StringRes;

import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.R;

public enum EBackupInterval {
    DAILY(1, R.string.daily),
    WEEKLY(7, R.string.weekly),
    MONTHLY(30, R.string.monthly);

    private final int days;
    private final int textResId;

    EBackupInterval(int days, @StringRes int textResId) {
        this.days = days;
        this.textResId = textResId;
    }

    public int getDays() {
        return days;
    }

    @Override
    public String toString() {
        return ApplicationInstance.get(textResId);
    }
}
