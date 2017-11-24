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

package de.dreier.mytargets.shared.models;

import android.support.annotation.Nullable;

public class TimerSettings {
    public boolean enabled;
    public boolean sound;
    public boolean vibrate;
    public int waitTime;
    public int shootTime;
    public int warnTime;

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimerSettings settings = (TimerSettings) o;
        return enabled == settings.enabled && sound == settings.sound &&
                vibrate == settings.vibrate && waitTime == settings.waitTime &&
                shootTime == settings.shootTime && warnTime == settings.warnTime;
    }

    @Override
    public int hashCode() {
        int result = (enabled ? 1 : 0);
        result = 31 * result + (sound ? 1 : 0);
        result = 31 * result + (vibrate ? 1 : 0);
        result = 31 * result + waitTime;
        result = 31 * result + shootTime;
        result = 31 * result + warnTime;
        return result;
    }
}
