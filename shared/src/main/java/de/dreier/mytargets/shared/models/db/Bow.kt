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

package de.dreier.mytargets.shared.models.db

import android.annotation.SuppressLint
import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.EBowType
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.Thumbnail
import de.dreier.mytargets.shared.utils.typeconverters.EBowTypeConverter
import de.dreier.mytargets.shared.utils.typeconverters.ThumbnailConverter
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class Bow(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        override var id: Long = 0,

        @Column
        var name: String = "",

        @Column(typeConverter = EBowTypeConverter::class)
        var type: EBowType? = EBowType.RECURVE_BOW,

        @Column
        var brand: String? = "",

        @Column
        var size: String? = "",

        @Column
        var braceHeight: String? = "",

        @Column
        var tiller: String? = "",

        @Column
        var limbs: String? = "",

        @Column
        var sight: String? = "",

        @Column
        var drawWeight: String? = "",

        @Column
        var stabilizer: String? = "",

        @Column
        var clicker: String? = "",

        @Column
        var button: String? = "",

        @Column
        var string: String? = "",

        @Column
        var nockingPoint: String? = "",

        @Column
        var letoffWeight: String? = "",

        @Column
        var arrowRest: String? = "",

        @Column
        var restHorizontalPosition: String? = "",

        @Column
        var restVerticalPosition: String? = "",

        @Column
        var restStiffness: String? = "",

        @Column
        var camSetting: String? = "",

        @Column
        var scopeMagnification: String? = "",

        @Column
        var description: String? = "",

        @Column(typeConverter = ThumbnailConverter::class)
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
