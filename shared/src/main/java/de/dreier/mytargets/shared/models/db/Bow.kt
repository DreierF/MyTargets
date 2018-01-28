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

package de.dreier.mytargets.shared.models.db

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import de.dreier.mytargets.shared.models.EBowType
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.Thumbnail
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Entity
data class Bow(
        @ColumnInfo(name = "_id")
        @PrimaryKey(autoGenerate = true)
        override var id: Long = 0,

        var name: String = "",
        var type: EBowType? = EBowType.RECURVE_BOW,
        var brand: String? = "",
        var size: String? = "",
        var braceHeight: String? = "",
        var tiller: String? = "",
        var limbs: String? = "",
        var sight: String? = "",
        var drawWeight: String? = "",
        var stabilizer: String? = "",
        var clicker: String? = "",
        var button: String? = "",
        var string: String? = "",
        var nockingPoint: String? = "",
        var letoffWeight: String? = "",
        var arrowRest: String? = "",
        var restHorizontalPosition: String? = "",
        var restVerticalPosition: String? = "",
        var restStiffness: String? = "",
        var camSetting: String? = "",
        var scopeMagnification: String? = "",
        var description: String? = "",
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        var thumbnail: Thumbnail? = null
) : IIdSettable, Parcelable {

    fun areAllPropertiesSet(): Boolean {
        return !size.isNullOrEmpty() &&
                !drawWeight.isNullOrEmpty() &&
                (!type!!.showLetoffWeight() || !letoffWeight.isNullOrEmpty()) &&
                (!type!!.showArrowRest() || !arrowRest.isNullOrEmpty()) &&
                (!type!!.showArrowRest() || !restVerticalPosition.isNullOrEmpty()) &&
                (!type!!.showArrowRest() || !restHorizontalPosition.isNullOrEmpty()) &&
                (!type!!.showArrowRest() || !restStiffness.isNullOrEmpty()) &&
                (!type!!.showCamSetting() || !camSetting.isNullOrEmpty()) &&
                (!type!!.showTiller() || !tiller.isNullOrEmpty()) &&
                (!type!!.showBraceHeight() || !braceHeight.isNullOrEmpty()) &&
                (!type!!.showLimbs() || !limbs.isNullOrEmpty()) &&
                (!type!!.showSight() || !sight.isNullOrEmpty()) &&
                (!type!!.showScopeMagnification() || !scopeMagnification.isNullOrEmpty()) &&
                (!type!!.showStabilizer() || !stabilizer.isNullOrEmpty()) &&
                (!type!!.showClicker() || !clicker.isNullOrEmpty()) &&
                (!type!!.showNockingPoint() || !nockingPoint.isNullOrEmpty()) &&
                !string.isNullOrEmpty() &&
                (!type!!.showButton() || !button.isNullOrEmpty()) &&
                !description.isNullOrEmpty()
    }
}
