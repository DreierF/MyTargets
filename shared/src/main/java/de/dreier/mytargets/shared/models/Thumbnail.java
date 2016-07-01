package de.dreier.mytargets.shared.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.raizlabs.android.dbflow.data.Blob;

import org.parceler.Parcel;

import java.io.File;

import de.dreier.mytargets.shared.utils.BitmapUtils;
import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;
import de.dreier.mytargets.shared.utils.ThumbnailUtils;

import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;

@Parcel
public class Thumbnail {
    private transient Drawable image;
    byte[] data;

    public Thumbnail() {
    }

    public Thumbnail(Blob data) {
        this.data = data.getBlob();
    }

    public Thumbnail(Bitmap bitmap) {
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap,
                ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL,
                ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        data = BitmapUtils.getBitmapAsByteArray(thumbnail);
        image = new RoundedAvatarDrawable(thumbnail);
    }

    public Thumbnail(File imageFile) {
        Bitmap thumbnail = ThumbnailUtils.createImageThumbnail(imageFile.getPath(), MICRO_KIND);
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
