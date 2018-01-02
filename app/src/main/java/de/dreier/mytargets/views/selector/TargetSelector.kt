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

package de.dreier.mytargets.views.selector

import android.content.Context
import android.util.AttributeSet
import de.dreier.mytargets.R
import de.dreier.mytargets.shared.models.Target

class TargetSelector @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ImageSelectorBase<Target>(context, attrs, TARGET_REQUEST_CODE, R.string.target_face) {

    companion object {
        const val TARGET_REQUEST_CODE = 12
    }
}
