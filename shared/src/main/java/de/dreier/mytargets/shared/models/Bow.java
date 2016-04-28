package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import org.parceler.Parcel;

import java.util.ArrayList;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.utils.BitmapUtils;
import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;

@Parcel
public class Bow implements IImageProvider, IIdSettable {
    public long id;
    public String name;
    public int type;
    public String brand;
    public String size;
    public String height;
    public String tiller;
    public String limbs;
    public String sight;
    public String drawWeight;
    public String stabilizer;
    public String clicker;
    public String description;
    public ArrayList<SightSetting> sightSettings;
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

    public Bitmap getImage(Context context) {
        if (image == null) {
            image = BitmapUtils.getBitmap(context, imageFile);
        }
        if (image == null) {
            context.getResources().getDrawable(R.drawable.recurve_bow);
        }
        return image;
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

    public void setImage(String imageFile, Bitmap imageBitmap) {
        this.imageFile = imageFile;
        image = imageBitmap;
        thumbnail = ThumbnailUtils.extractThumbnail(image, 100, 100);
        thumb = BitmapUtils.getBitmapAsByteArray(thumbnail);
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Bow &&
                getClass().equals(another.getClass()) &&
                id == ((Bow) another).id;
    }
}
