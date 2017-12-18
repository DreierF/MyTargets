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

package de.dreier.mytargets.shared.models

import android.support.annotation.StringRes
import de.dreier.mytargets.shared.R

import de.dreier.mytargets.shared.SharedApplicationInstance

enum class ETargetType constructor(@StringRes private val nameRes: Int) {
    TARGET(R.string.target_type_target_archery),
    FIELD(R.string.target_type_field_archery),
    THREE_D(R.string.target_type_3d_archery);

    override fun toString(): String {
        return SharedApplicationInstance.getStr(nameRes)
    }
}
