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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet

import de.dreier.mytargets.features.arrows.ArrowListActivity
import de.dreier.mytargets.features.arrows.EditArrowFragment
import de.dreier.mytargets.shared.models.dao.ArrowDAO
import de.dreier.mytargets.shared.models.db.Arrow

class ArrowSelector @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ImageSelectorBase<Arrow>(context, attrs, ARROW_REQUEST_CODE) {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ARROW_ADD_REQUEST_CODE) {
            setItemId(null)
        }
    }

    fun setItemId(arrowId: Long?) {
        var item: Arrow? = null
        if (arrowId != null) {
            item = ArrowDAO.loadArrowOrNull(arrowId)
        }
        if (item == null) {
            val all = ArrowDAO.loadArrows()
            if (all.isNotEmpty()) {
                item = all[0]
            }
        }
        setItem(item)
    }

    companion object {
        const val ARROW_REQUEST_CODE = 5
        const val ARROW_ADD_REQUEST_CODE = 6
    }
}
