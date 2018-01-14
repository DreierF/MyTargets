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

package de.dreier.mytargets.shared.models

import android.os.Parcel
import android.os.Parcelable

data class TimerSettings(
        var enabled: Boolean = false,
        var sound: Boolean = false,
        var vibrate: Boolean = false,
        var waitTime: Int = 0,
        var shootTime: Int = 0,
        var warnTime: Int = 0) : Parcelable {
    constructor(source: Parcel) : this(
            1 == source.readInt(),
            1 == source.readInt(),
            1 == source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt((if (enabled) 1 else 0))
        writeInt((if (sound) 1 else 0))
        writeInt((if (vibrate) 1 else 0))
        writeInt(waitTime)
        writeInt(shootTime)
        writeInt(warnTime)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TimerSettings> = object : Parcelable.Creator<TimerSettings> {
            override fun createFromParcel(source: Parcel): TimerSettings = TimerSettings(source)
            override fun newArray(size: Int): Array<TimerSettings?> = arrayOfNulls(size)
        }
    }
}
