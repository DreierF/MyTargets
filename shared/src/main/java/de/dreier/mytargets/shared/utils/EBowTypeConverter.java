package de.dreier.mytargets.shared.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import de.dreier.mytargets.shared.models.EBowType;

public final class EBowTypeConverter extends TypeConverter<Integer, EBowType> {

    @Override
    public Integer getDBValue(EBowType model) {
        if (model != null) {
            return model.getId();
        }

        return null;
    }

    @Override
    public EBowType getModelValue(Integer data) {
        if (data != null) {
            return EBowType.fromId(data);
        }

        return null;
    }

}