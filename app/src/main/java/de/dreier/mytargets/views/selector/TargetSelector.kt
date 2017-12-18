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
import de.dreier.mytargets.features.training.target.TargetActivity
import de.dreier.mytargets.features.training.target.TargetListFragment
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.utils.IntentWrapper

class TargetSelector @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ImageSelectorBase<Target>(context, attrs) {
    private var fixedType: TargetListFragment.EFixedType = TargetListFragment.EFixedType.NONE

    override val defaultIntent: IntentWrapper
        get() {
            val i = super.defaultIntent
            i.with(TargetListFragment.FIXED_TYPE, fixedType.name)
            return i
        }

    init {
        defaultActivity = TargetActivity::class.java
        requestCode = TARGET_REQUEST_CODE
    }

    fun setFixedType(fixedType: TargetListFragment.EFixedType) {
        this.fixedType = fixedType
    }

    override fun bindView(item: Target) {
        super.bindView(item)
        setTitle(R.string.target_face)
    }

    companion object {
        val TARGET_REQUEST_CODE = 12
    }
}
