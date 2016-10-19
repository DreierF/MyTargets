package de.dreier.mytargets.shared.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import de.dreier.mytargets.shared.models.EWeather;

public final class EWeatherConverter extends TypeConverter<Integer, EWeather> {

    @Override
    public Integer getDBValue(EWeather model) {
        if (model != null) {
            return model.getValue();
        }

        return null;
    }

    @Override
    public EWeather getModelValue(Integer data) {
        if (data != null) {
            return EWeather.getOfValue(data);
        }

        return null;
    }

}