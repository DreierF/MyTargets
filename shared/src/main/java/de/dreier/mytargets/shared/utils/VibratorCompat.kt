/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.shared.utils

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object VibratorCompat {
    @Suppress("DEPRECATION")
    fun vibrate(v: Vibrator, durationInMs: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(durationInMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(durationInMs)
        }
    }

    @Suppress("DEPRECATION")
    fun vibrate(v: Vibrator, timings: LongArray, repeat: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(timings, repeat))
        } else {
            v.vibrate(timings, repeat)
        }
    }

}
