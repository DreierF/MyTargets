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

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.SharedApplicationInstance

enum class EBowType constructor(
        @StringRes private val nameRes: Int,
        @DrawableRes  val drawable: Int
) {
    RECURVE_BOW(R.string.recurve_bow, R.drawable.ic_compat_bow_recurve_white_24dp),
    COMPOUND_BOW(R.string.compound_bow, R.drawable.ic_compat_bow_compound_white_24dp),
    LONG_BOW(R.string.long_bow, R.drawable.ic_compat_bow_long_white_24dp),
    BARE_BOW(R.string.bare_bow, R.drawable.ic_compat_bow_bare_white_24dp),
    HORSE_BOW(R.string.horse_bow, R.drawable.ic_compat_bow_horse_white_24dp),
    YUMI(R.string.yumi, R.drawable.ic_compat_bow_yumi_white_24dp);

    override fun toString(): String {
        return SharedApplicationInstance.getStr(nameRes)
    }

    fun showSize() = true

    fun showBraceHeight() = this != COMPOUND_BOW

    fun showTiller() = this == RECURVE_BOW || this == BARE_BOW

    fun showLimbs() = this == RECURVE_BOW || this == BARE_BOW

    fun showSight() = this == RECURVE_BOW || this == COMPOUND_BOW

    fun showDrawWeight() = true

    fun showStabilizer() = this == RECURVE_BOW || this == COMPOUND_BOW

    fun showClicker() = this == RECURVE_BOW

    fun showButton() = this == RECURVE_BOW

    fun showNockingPoint() = this == RECURVE_BOW || this == BARE_BOW

    fun showString() = true

    fun showLetoffWeight() = this == COMPOUND_BOW

    fun showArrowRest() = this == RECURVE_BOW || this == COMPOUND_BOW

    fun showCamSetting() = this == COMPOUND_BOW

    fun showScopeMagnification() = this == COMPOUND_BOW

    companion object {
        fun fromId(id: Int) = EBowType.values()[id]
    }
}
