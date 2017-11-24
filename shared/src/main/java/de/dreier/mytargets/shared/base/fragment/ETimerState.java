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

package de.dreier.mytargets.shared.base.fragment;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import de.dreier.mytargets.shared.R;

public enum ETimerState {
    WAIT_FOR_START(R.color.timer_red, 0),
    PREPARATION(R.color.timer_red, 2),
    SHOOTING(R.color.timer_green, 1),
    COUNTDOWN(R.color.timer_orange, 0),
    FINISHED(R.color.timer_red, 3),
    EXIT(R.color.timer_red, 0);

    public int color;
    public int signalCount;

    ETimerState(@ColorRes int color, int signalCount) {
        this.color = color;
        this.signalCount = signalCount;
    }

    @NonNull
    public ETimerState getNext() {
        switch (this) {
            case WAIT_FOR_START:
                return PREPARATION;
            case PREPARATION:
                return SHOOTING;
            case SHOOTING:
                return COUNTDOWN;
            case COUNTDOWN:
                return FINISHED;
            case FINISHED:
                return EXIT;
            default:
                return WAIT_FOR_START;
        }
    }
}
