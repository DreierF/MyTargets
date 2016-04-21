package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.utils.BitmapUtils;
import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;

@Parcel
public class Arrow implements IImageProvider, IIdSettable {
    public long id;
    public String name;
    public String length;
    public String material;
    public String spine;
    public String weight;
    public String tipWeight;
    public String vanes;
    public String nock;
    public String comment;
    public List<ArrowNumber> numbers = new ArrayList<>();
    public byte[] thumb;
    public String imageFile;

    private transient Bitmap thumbnail;
    private transient Bitmap image;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Drawable getDrawable() {
        if (thumbnail == null) {
            thumbnail = BitmapFactory.decodeByteArray(thumb, 0, thumb.length);
        }
        return new RoundedAvatarDrawable(thumbnail);
    }

    @Override
    public Drawable getDrawable(Context context) {
        return getDrawable();
    }

    @Override
    public String getName(Context context) {
        return name;
    }

    public Bitmap getImage(Context context) {
        if (image == null) {
            BitmapUtils.getBitmap(context, imageFile);
        }
        return image;
    }

    public void setImage(String imageFile, Bitmap imageBitmap) {
        this.imageFile = imageFile;
        image = imageBitmap;
        thumbnail = ThumbnailUtils.extractThumbnail(image, 100, 100);
        thumb = BitmapUtils.getBitmapAsByteArray(thumbnail);
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Arrow &&
                getClass().equals(another.getClass()) &&
                id == ((Arrow) another).id;
    }
}