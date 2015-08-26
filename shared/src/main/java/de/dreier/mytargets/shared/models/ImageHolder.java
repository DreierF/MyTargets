/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.dreier.mytargets.shared.utils.BitmapUtils;

public class ImageHolder extends IdProvider {

    public byte[] thumb;
    public String imageFile;
    private transient Bitmap thumbnail;
    private transient Bitmap image;

    public Bitmap getThumbnail() {
        if (thumbnail == null) {
            thumbnail = BitmapFactory.decodeByteArray(thumb, 0, thumb.length);
        }
        return thumbnail;
    }

    public Bitmap getImage(Context context) {
        if (image == null) {
            try {
                if (imageFile != null) {
                    if (imageFile.contains("/")) {
                        imageFile = imageFile.substring(imageFile.lastIndexOf("/") + 1);
                    }
                    Log.d("getImage", imageFile);
                    FileInputStream in = context.openFileInput(imageFile);
                    image = BitmapFactory.decodeStream(in);
                } else {
                    image = null;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    public void setImage(String imageFile, Bitmap imageBitmap) {
        this.imageFile = imageFile;
        image = imageBitmap;
        thumbnail = ThumbnailUtils.extractThumbnail(image, 100, 100);
        thumb = BitmapUtils.getBitmapAsByteArray(thumbnail);
    }
}
