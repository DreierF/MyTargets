package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;

public class Bow implements IImageProvider, IIdSettable, Comparable<Bow> {
    public long id = -1;
    public String name = "";
    public EBowType type = EBowType.RECURVE_BOW;
    public String brand = "";
    public String size = "";
    public String braceHeight = "";
    public String tiller = "";
    public String limbs = "";
    public String sight = "";
    public String drawWeight = "";
    public String stabilizer = "";
    public String clicker = "";
    public String description = "";
    public ArrayList<SightSetting> sightSettings = new ArrayList<>();
    public byte[] thumb;
    public String imageFile;
    private transient Bitmap thumbnail;

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
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Bow &&
                getClass().equals(another.getClass()) &&
                id == ((Bow) another).id;
    }

    @Override
    public int compareTo(@NonNull Bow another) {
        final int result = getName().compareTo(another.getName());
        return result == 0 ? (int) (id - another.id) : result;
    }
}
