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

package de.dreier.mytargets.features.arrows

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableFloat
import android.databinding.ObservableInt
import android.text.TextUtils
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.MILLIMETER
import de.dreier.mytargets.shared.models.Thumbnail
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.ArrowImage


class EditArrowViewModel(app: ApplicationInstance) : AndroidViewModel(app) {
    val arrowId = MutableLiveData<Long?>()

    val arrow: LiveData<Arrow?>
    var images: LiveData<List<ArrowImage>>

    var name = ObservableField<String>(getApplication<ApplicationInstance>().getString(R.string.my_arrow))
    var maxArrowNumber = ObservableInt(12)
    var length = ObservableField<String>("")
    var material = ObservableField<String>("")
    var spine = ObservableField<String>("")
    var weight = ObservableField<String>("")
    var tipWeight = ObservableField<String>("")
    var vanes = ObservableField<String>("")
    var nock = ObservableField<String>("")
    var comment = ObservableField<String>("")
    var diameterValue = ObservableFloat(5f)
    var diameterUnit = ObservableField<Dimension.Unit>(MILLIMETER)

    var showAll = ObservableBoolean(false)
    var diameterErrorText = ObservableField<String>("")

    private val arrowDAO = ApplicationInstance.db.arrowDAO()

    init {
        arrow = Transformations.map(arrowId) { id ->
            if (id == null) {
                null
            } else {
                val arrow = arrowDAO.loadArrow(id)
                setFromArrow(arrow)
                arrow
            }
        }
        images = Transformations.map(arrowId, {
            id -> if(id == null)
                mutableListOf()
            else
                arrowDAO.loadArrowImages(id)
        })
    }

    private fun setFromArrow(arrow: Arrow) {
        name.set(arrow.name)
        maxArrowNumber.set(arrow.maxArrowNumber)
        length.set(arrow.length)
        material.set(arrow.material)
        spine.set(arrow.spine)
        weight.set(arrow.weight)
        tipWeight.set(arrow.tipWeight)
        vanes.set(arrow.vanes)
        nock.set(arrow.nock)
        comment.set(arrow.comment)
        diameterValue.set(arrow.diameter.value)
        diameterUnit.set(arrow.diameter.unit)
    }

    fun save(thumb: Thumbnail, imageFiles: List<ArrowImage>): Boolean {
        if (!validateInput()) {
            return false
        }
        val arrow = Arrow()
        arrow.id = arrowId.value ?: 0
        arrow.name = name.get() ?: ""
        arrow.maxArrowNumber = maxArrowNumber.get()
        arrow.length = length.get()
        arrow.material = material.get()
        arrow.spine = spine.get()
        arrow.weight = weight.get()
        arrow.tipWeight = tipWeight.get()
        arrow.vanes = vanes.get()
        arrow.nock = nock.get()
        arrow.comment = comment.get()
        arrow.thumbnail = thumb
        arrow.diameter = Dimension(diameterValue.get(), diameterUnit.get())
        arrowDAO.saveArrow(arrow, imageFiles)
        return true
    }

    private fun validateInput(): Boolean {
        if (diameterUnit.get() == MILLIMETER) {
            if (diameterValue.get() !in 1..20) {
                diameterErrorText.set(getApplication<ApplicationInstance>().getString(R.string.not_within_expected_range_mm))
                return false
            }
        } else {
            if (diameterValue.get() !in 0f..1f) {
                diameterErrorText.set(getApplication<ApplicationInstance>().getString(R.string.not_within_expected_range_inch))
                return false
            }
        }
        diameterErrorText.set("")
        return true
    }

    fun setArrowId(arrowId: Long?) {
        this.arrowId.value = arrowId
    }

    fun areAllPropertiesSet(): Boolean {
        return !TextUtils.isEmpty(length.get()) &&
                !TextUtils.isEmpty(material.get()) &&
                !TextUtils.isEmpty(spine.get()) &&
                !TextUtils.isEmpty(weight.get()) &&
                !TextUtils.isEmpty(tipWeight.get()) &&
                !TextUtils.isEmpty(vanes.get()) &&
                !TextUtils.isEmpty(nock.get()) &&
                !TextUtils.isEmpty(comment.get())
    }
}
