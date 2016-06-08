package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

public interface IImageProvider {
    Drawable getDrawable(Context context);
    String getName();
}
