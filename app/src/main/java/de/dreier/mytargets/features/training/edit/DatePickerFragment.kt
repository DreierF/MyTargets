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

package de.dreier.mytargets.features.training.edit

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import org.threeten.bp.LocalDate

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val date = arguments!!.getSerializable(ARG_CURRENT_DATE) as LocalDate

        // Create a new instance of DatePickerDialog and return it
        val listener = targetFragment as DatePickerDialog.OnDateSetListener?
        return DatePickerDialog(
            activity!!, listener, date.year,
            date.monthValue - 1, date.dayOfMonth
        )
    }

    companion object {
        private const val ARG_CURRENT_DATE = "current_date"

        fun newInstance(date: LocalDate): DatePickerFragment {
            val datePickerDialog = DatePickerFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_CURRENT_DATE, date)
            datePickerDialog.arguments = bundle
            return datePickerDialog
        }
    }
}
