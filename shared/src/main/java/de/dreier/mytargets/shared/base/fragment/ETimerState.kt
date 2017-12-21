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

package de.dreier.mytargets.shared.base.fragment

import android.support.annotation.ColorRes

import de.dreier.mytargets.shared.R

enum class ETimerState constructor(@ColorRes var color: Int, var signalCount: Int) {
    WAIT_FOR_START(R.color.timer_red, 0),
    PREPARATION(R.color.timer_red, 2),
    SHOOTING(R.color.timer_green, 1),
    COUNTDOWN(R.color.timer_orange, 0),
    FINISHED(R.color.timer_red, 3),
    EXIT(R.color.timer_red, 0);

    val next: ETimerState
        get() {
            return when (this) {
                WAIT_FOR_START -> PREPARATION
                PREPARATION -> SHOOTING
                SHOOTING -> COUNTDOWN
                COUNTDOWN -> FINISHED
                FINISHED -> EXIT
                EXIT -> WAIT_FOR_START
            }
        }
}
