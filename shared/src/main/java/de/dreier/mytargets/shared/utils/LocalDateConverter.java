package de.dreier.mytargets.shared.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.LocalDate;

public final class LocalDateConverter extends TypeConverter<Long, LocalDate> {

    @Override
    public Long getDBValue(LocalDate model) {
        if (model != null) {
            return model.toDate().getTime();
        }

        return null;
    }

    @Override
    public LocalDate getModelValue(Long data) {
        if (data != null) {
            return new LocalDate(data);
        }

        return null;
    }

}