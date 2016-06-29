package de.dreier.mytargets.shared.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.raizlabs.android.dbflow.data.Blob;

import de.dreier.mytargets.shared.utils.BitmapUtils;
import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;

public class Thumbnail {
    private transient Drawable image;
    byte[] data;

    public Thumbnail(Blob data) {
        this.data = data.getBlob();
    }

    public Thumbnail(Bitmap thumbnail) {
        data = BitmapUtils.getBitmapAsByteArray(thumbnail);
        image = new RoundedAvatarDrawable(thumbnail);
    }

    public Drawable getRoundDrawable() {
        if (image == null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            image = new RoundedAvatarDrawable(bitmap);
        }
        return image;
    }

    public Blob getBlob() {
        return new Blob(data);
    }
}
