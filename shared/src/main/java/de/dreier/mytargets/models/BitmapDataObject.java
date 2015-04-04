/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

public class BitmapDataObject implements Serializable {
    static final long serialVersionUID = 46L;
    public byte[] imageByteArray;

    public BitmapDataObject() {
    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }
}
