package de.dreier.mytargets.shared.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.DateTime;

public final class DateTimeConverter extends TypeConverter<Long, DateTime> {

    @Override
    public Long getDBValue(DateTime model) {
        if (model != null) {
            return model.toDate().getTime();
        }

        return null;
    }

    @Override
    public DateTime getModelValue(Long data) {
        if (data != null) {
            return new DateTime(data);
        }

        return null;
    }

}