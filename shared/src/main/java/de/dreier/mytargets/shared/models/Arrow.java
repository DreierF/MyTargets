package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;

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
}