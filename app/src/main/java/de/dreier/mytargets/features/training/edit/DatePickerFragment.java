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

package de.dreier.mytargets.features.training.edit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import junit.framework.Assert;

import org.threeten.bp.LocalDate;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_CURRENT_DATE = "current_date";

    @NonNull
    public static DatePickerFragment newInstance(LocalDate date) {
        DatePickerFragment datePickerDialog = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CURRENT_DATE, date);
        datePickerDialog.setArguments(bundle);
        return datePickerDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        LocalDate date = (LocalDate) getArguments().getSerializable(ARG_CURRENT_DATE);
        Assert.assertNotNull(date);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getTargetFragment();
        return new DatePickerDialog(getActivity(), listener, date.getYear(),
                date.getMonthValue() - 1, date.getDayOfMonth());
    }
}
