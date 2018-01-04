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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.R
import de.dreier.mytargets.base.fragments.EditWithImageFragmentBase
import de.dreier.mytargets.databinding.FragmentEditArrowBinding
import de.dreier.mytargets.shared.models.db.ArrowImage
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.getLongOrNull

class EditArrowFragment : EditWithImageFragmentBase<ArrowImage>(R.drawable.arrows) {

    private lateinit var contentBinding: FragmentEditArrowBinding
    private lateinit var viewModel: EditArrowViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        contentBinding = FragmentEditArrowBinding.inflate(inflater, binding.content, true)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditArrowViewModel::class.java)
        viewModel.setArrowId(arguments.getLongOrNull(ARROW_ID))
        contentBinding.arrow = viewModel
        contentBinding.moreFields.setOnClickListener {
            viewModel.showAll.set(true)
        }
        viewModel.arrow.observe(this, Observer { arrow ->
            if (arrow == null) {
                return@Observer
            }
            ToolbarUtils.setTitle(this@EditArrowFragment, arrow.name)
        })
        viewModel.images.observe(this, Observer { images ->
            if (images != null) {
                imageFiles = images
                loadImage(imageFile)
            }
        })
    }
    
    override fun wrapImage(imageFile: String): List<ArrowImage> {
        return listOf(ArrowImage(fileName = imageFile))
    }

    public override fun onSave() {
        super.onSave()
        if (viewModel.save(thumbnail)) {
            navigationController.finish()
        }
    }

    companion object {
        const val ARROW_ID = "arrow_id"
    }
}
