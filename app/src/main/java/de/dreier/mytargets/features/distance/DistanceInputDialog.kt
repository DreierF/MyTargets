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

package de.dreier.mytargets.features.distance

import android.app.AlertDialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.text.InputType
import android.view.LayoutInflater
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.DialogCommentBinding

class DistanceInputDialog {

    interface OnClickListener {
        fun onOkClickListener(input: String)
    }

    class Builder(private val context: Context) {
        private var clickListener: OnClickListener? = null
        private var unit: String? = null

        fun show() {
            val inflater = LayoutInflater.from(context)
            val binding = DataBindingUtil
                    .inflate<DialogCommentBinding>(inflater, R.layout.dialog_comment, null, false)
            binding.shotComment.inputType = InputType.TYPE_CLASS_NUMBER
            binding.unit.text = unit
            val shotComment = binding.shotComment

            AlertDialog.Builder(context)
                    .setTitle(R.string.distance)
                    .setView(binding.root)
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        val s = shotComment.text.toString()
                        clickListener?.onOkClickListener(s)
                        dialog.dismiss()
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    .show()
        }

        fun setOnClickListener(listener: OnClickListener): Builder {
            clickListener = listener
            return this
        }

        fun setUnit(unit: String): Builder {
            this.unit = unit
            return this
        }
    }
}
