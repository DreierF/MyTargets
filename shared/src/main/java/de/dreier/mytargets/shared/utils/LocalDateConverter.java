package de.dreier.mytargets.shared.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.LocalDate;

public final class LocalDateConverter extends TypeConverter<String, LocalDate> {

    @Override
    public String getDBValue(LocalDate model) {
        if (model != null) {
            return model.toString();
        }

        return null;
    }

    @Override
    public LocalDate getModelValue(String data) {
        if (data != null) {
            return LocalDate.parse(data);
        }

        return null;
    }

}