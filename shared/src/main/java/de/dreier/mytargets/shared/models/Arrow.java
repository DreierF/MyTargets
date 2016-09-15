package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;

public class Arrow implements IImageProvider, IIdSettable, Comparable<Arrow> {
    public long id = -1;
    public String name = "";
    public String length = "";
    public String material = "";
    public String spine = "";
    public String weight = "";
    public String tipWeight = "";
    public String vanes = "";
    public String nock = "";
    public String comment = "";
    public Dimension diameter = new Dimension(5, Dimension.Unit.MILLIMETER);
    public ArrayList<ArrowNumber> numbers = new ArrayList<>();
    public byte[] thumb;
    public String imageFile;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Drawable getDrawable() {
        return new RoundedAvatarDrawable(BitmapFactory.decodeByteArray(thumb, 0, thumb.length));
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
        return another instanceof Arrow &&
                getClass().equals(another.getClass()) &&
                id == ((Arrow) another).id;
    }

    @Override
    public int compareTo(@NonNull Arrow another) {
        final int result = getName().compareTo(another.getName());
        return result == 0 ? (int) (id - another.id) : result;
    }
}
