package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.util.ArrayList;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.utils.BitmapUtils;

public class Bow implements IIdSettable {
    public static final String ID = "_id";
    static final long serialVersionUID = 52L;

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
    protected long id;
    private transient Bitmap thumbnail;
    private transient Bitmap image;

    public Bitmap getImage(Context context) {
        Bitmap img = getImage(context);
        if (img == null) {
            context.getResources().getDrawable(R.drawable.recurve_bow);
        }
        return img;
    }

    public Bitmap getThumbnail() {
        if (thumbnail == null) {
            thumbnail = BitmapFactory.decodeByteArray(thumb, 0, thumb.length);
        }
        return thumbnail;
    }

    public void setImage(String imageFile, Bitmap imageBitmap) {
        this.imageFile = imageFile;
        image = imageBitmap;
        thumbnail = ThumbnailUtils.extractThumbnail(image, 100, 100);
        thumb = BitmapUtils.getBitmapAsByteArray(thumbnail);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Bow &&
                getClass().equals(another.getClass()) &&
                id == ((Bow) another).id;
    }
}
