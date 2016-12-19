package de.dreier.mytargets.shared.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import com.raizlabs.android.dbflow.data.Blob;

import org.parceler.Parcel;

import java.io.File;

import de.dreier.mytargets.shared.utils.BitmapUtils;
import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;

@Parcel
public class Thumbnail {
    /**
     * Constant used to indicate the dimension of micro thumbnail.
     */
    private static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;
    private transient Drawable image;
    byte[] data;


    public Thumbnail() {
    }

    public Thumbnail(Blob data) {
        this.data = data.getBlob();
    }

    public Thumbnail(Bitmap bitmap) {
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap,
                TARGET_SIZE_MICRO_THUMBNAIL,
                TARGET_SIZE_MICRO_THUMBNAIL,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        data = BitmapUtils.getBitmapAsByteArray(thumbnail);
        image = new RoundedAvatarDrawable(thumbnail);
    }

    public Thumbnail(File imageFile) {
        Bitmap thumbnail = ThumbnailUtils
                .extractThumbnail(BitmapFactory.decodeFile(imageFile.getPath()),
                        TARGET_SIZE_MICRO_THUMBNAIL, TARGET_SIZE_MICRO_THUMBNAIL);
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
