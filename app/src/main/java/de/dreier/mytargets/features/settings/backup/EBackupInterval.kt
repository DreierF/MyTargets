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

package de.dreier.mytargets.features.settings.backup

import android.support.annotation.StringRes
import de.dreier.mytargets.R
import de.dreier.mytargets.shared.SharedApplicationInstance

enum class EBackupInterval constructor(val days: Int, @StringRes private val textResId: Int) {
    DAILY(1, R.string.daily),
    WEEKLY(7, R.string.weekly),
    MONTHLY(30, R.string.monthly);

    override fun toString(): String {
        return SharedApplicationInstance.getStr(textResId)
    }
}
