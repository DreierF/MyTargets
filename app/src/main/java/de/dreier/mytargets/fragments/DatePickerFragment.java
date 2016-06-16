package de.dreier.mytargets.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import org.joda.time.LocalDate;

public class DatePickerFragment extends DialogFragment {

    static final String ARG_CURRENT_DATE = "current_date";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        LocalDate date = (LocalDate) getArguments().getSerializable(ARG_CURRENT_DATE);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getTargetFragment();
        return new DatePickerDialog(getActivity(), listener, date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());
    }
}
