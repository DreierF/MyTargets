package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

import de.dreier.mytargets.shared.R;

public class Bow extends ImageHolder {
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
    public String description;
    public ArrayList<SightSetting> sightSettings;

    @Override
    public Bitmap getImage(Context context) {
        Bitmap img = super.getImage(context);
        if (img == null) {
            context.getResources().getDrawable(R.drawable.recurve_bow);
        }
        return img;
    }
}
