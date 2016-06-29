package de.dreier.mytargets.shared.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import de.dreier.mytargets.shared.models.Dimension;

public final class DimensionConverter extends TypeConverter<String, Dimension> {

    @Override
    public String getDBValue(Dimension model) {
        if (model != null) {
            return model.value + " " + model.unit;
        }

        return null;
    }

    @Override
    public Dimension getModelValue(String data) {
        if (data != null) {
            int index = data.indexOf(' ');
            final String value = data.substring(0, index);
            final String unit = data.substring(index + 1);
            return new Dimension(Integer.parseInt(value), Dimension.Unit.from(unit));
        }

        return null;
    }

}