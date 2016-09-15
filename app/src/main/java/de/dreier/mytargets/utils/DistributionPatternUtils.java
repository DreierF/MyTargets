/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.dreier.mytargets.models.ArrowStatistic;
import de.dreier.mytargets.shared.targets.TargetDrawable;

public class DistributionPatternUtils {

    public static void createDistributionPatternImageFile(int size, File f, ArrowStatistic statistic) throws FileNotFoundException {
        Bitmap b = getDistributionPatternBitmap(size, statistic);
        if (b == null) {
            return;
        }

        final FileOutputStream fOut = new FileOutputStream(f);
        try {
            b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getDistributionPatternBitmap(int size, ArrowStatistic statistic) {
        Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        TargetDrawable target = statistic.target.getDrawable();
        target.setBounds(new Rect(0, 0, size, size));
        target.draw(canvas);
        target.drawArrows(canvas, statistic.shots, false);
        //TODO draw average on every spot
        return b;
    }
}
