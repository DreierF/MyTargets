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

package de.dreier.mytargets.features.settings

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.widget.DatePicker

import org.threeten.bp.LocalDate

class DatePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat(), DialogPreference.TargetFragment, DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val pref = preference as DatePreference
        return DatePickerDialog(context!!, this,
                pref.date.year,
                pref.date.monthValue - 1,
                pref.date.dayOfMonth)
    }

    override fun onDialogClosed(b: Boolean) {

    }

    override fun findPreference(charSequence: CharSequence): Preference {
        return preference
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val pref = preference as DatePreference
        pref.date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
        if (pref.callChangeListener(pref.date)) {
            pref.persistDateValue(pref.date)
        }
    }

    companion object {
        fun newInstance(key: String): DialogFragment {
            val dialogFragment = DatePreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString("key", key)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }
}
