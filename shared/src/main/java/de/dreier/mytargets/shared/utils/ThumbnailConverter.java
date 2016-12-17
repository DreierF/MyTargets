package de.dreier.mytargets.shared.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.data.Blob;

import de.dreier.mytargets.shared.models.Thumbnail;

public final class ThumbnailConverter extends TypeConverter<Blob, Thumbnail> {

    @Override
    public Blob getDBValue(Thumbnail model) {
        if (model != null) {
            return model.getBlob();
        }

        return null;
    }

    @Override
    public Thumbnail getModelValue(Blob data) {
        if (data != null) {
            return new Thumbnail(data);
        }

        return null;
    }

}