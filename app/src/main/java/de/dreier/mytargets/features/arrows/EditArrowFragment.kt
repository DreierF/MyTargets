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

package de.dreier.mytargets.features.arrows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evernote.android.state.State
import de.dreier.mytargets.R
import de.dreier.mytargets.base.fragments.EditWithImageFragmentBase
import de.dreier.mytargets.databinding.FragmentEditArrowBinding
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.INCH
import de.dreier.mytargets.shared.models.Dimension.Unit.MILLIMETER
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.ArrowImage
import de.dreier.mytargets.utils.ToolbarUtils
import java.lang.Integer.parseInt

class EditArrowFragment : EditWithImageFragmentBase<ArrowImage>(R.drawable.arrows) {
    @State
    lateinit var arrow: Arrow
    private lateinit var contentBinding: FragmentEditArrowBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        contentBinding = FragmentEditArrowBinding.inflate(inflater, binding.content, true)
        contentBinding.moreFields.setOnClickListener { contentBinding.showAll = true }

        if (savedInstanceState == null) {
            val bundle = arguments
            if (bundle != null && bundle.containsKey(ARROW_ID)) {
                arrow = Arrow[bundle.getLong(ARROW_ID)]!!
            } else {
                // Set to default values
                arrow = Arrow()
                arrow.name = getString(R.string.my_arrow)
            }

            imageFiles = arrow.loadImages()
            contentBinding.diameterUnit.setSelection(
                    if (arrow.diameter.unit === MILLIMETER) 0 else 1)
        }
        ToolbarUtils.setTitle(this, arrow.name)
        contentBinding.arrow = arrow
        contentBinding.showAll = false
        loadImage(imageFile)
        return rootView
    }

    override fun wrapImage(imageFile: String): List<ArrowImage> {
        return listOf(ArrowImage(fileName = imageFile))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        arrow = buildArrow()
        super.onSaveInstanceState(outState)
    }

    public override fun onSave() {
        super.onSave()
        if (!validateInput()) {
            return
        }
        buildArrow().save()
        finish()
    }

    private fun validateInput(): Boolean {
        val diameterValue: Float
        try {
            diameterValue = java.lang.Float.parseFloat(contentBinding.diameter.text.toString())
        } catch (ignored: NumberFormatException) {
            contentBinding.diameterTextInputLayout.error = getString(R.string.invalid_decimal_number)
            return false
        }

        val selectedUnit = contentBinding.diameterUnit.selectedItemPosition
        val diameterUnit = if (selectedUnit == 0) MILLIMETER else INCH
        if (diameterUnit === MILLIMETER) {
            if (diameterValue < 1 || diameterValue > 20) {
                contentBinding.diameterTextInputLayout.error = getString(R.string.not_within_expected_range_mm)
                return false
            }
        } else {
            if (diameterValue < 0 || diameterValue > 1) {
                contentBinding.diameterTextInputLayout.error = getString(R.string.not_within_expected_range_inch)
                return false
            }
        }
        contentBinding.diameterTextInputLayout.error = null
        return true
    }

    private fun buildArrow(): Arrow {
        arrow.name = contentBinding.name.text.toString()
        arrow.maxArrowNumber = parseInt(contentBinding.maxArrowNumber.text.toString())
        arrow.length = contentBinding.length.text.toString()
        arrow.material = contentBinding.material.text.toString()
        arrow.spine = contentBinding.spine.text.toString()
        arrow.weight = contentBinding.weight.text.toString()
        arrow.tipWeight = contentBinding.tipWeight.text.toString()
        arrow.vanes = contentBinding.vanes.text.toString()
        arrow.nock = contentBinding.nock.text.toString()
        arrow.comment = contentBinding.comment.text.toString()
        arrow.images = imageFiles
        arrow.thumbnail = thumbnail
        val diameterValue = contentBinding.diameter.text.toString().toFloatOrNull() ?: 5f
        val selectedUnit = contentBinding.diameterUnit.selectedItemPosition
        val diameterUnit = if (selectedUnit == 0) MILLIMETER else INCH
        arrow.diameter = Dimension(diameterValue, diameterUnit)
        return arrow
    }

    companion object {
        const val ARROW_ID = "arrow_id"
    }
}
