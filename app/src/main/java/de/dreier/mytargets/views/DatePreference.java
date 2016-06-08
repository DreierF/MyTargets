package de.dreier.mytargets.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import org.joda.time.LocalDate;

public class DatePreference extends DialogPreference {
    public LocalDate date = LocalDate.now();

    public DatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String value;
        if (restoreValue) {
            if (defaultValue == null) {
                value = getPersistedString(LocalDate.now().toString());
            } else {
                value = getPersistedString(defaultValue.toString());
            }
        } else {
            value = defaultValue.toString();
        }

        date = LocalDate.parse(value);
    }

    public void persistDateValue(LocalDate value) {
        persistString(value.toString());
    }
}