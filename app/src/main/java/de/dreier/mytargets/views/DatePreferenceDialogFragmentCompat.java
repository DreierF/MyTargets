/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.widget.DatePicker;

import org.joda.time.LocalDate;

public class DatePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment, DatePickerDialog.OnDateSetListener {

    @NonNull
    public static DialogFragment newInstance(String key) {
        DialogFragment dialogFragment = new DatePreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", key);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePreference pref = (DatePreference) getPreference();
        return new DatePickerDialog(getContext(), this,
                pref.date.getYear(),
                pref.date.getMonthOfYear() - 1,
                pref.date.getDayOfMonth());
    }

    @Override
    public void onDialogClosed(boolean b) {

    }

    @Override
    public Preference findPreference(CharSequence charSequence) {
        return getPreference();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        DatePreference pref = (DatePreference) getPreference();
        pref.date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
        if (pref.callChangeListener(pref.date)) {
            pref.persistDateValue(pref.date);
        }
    }
}