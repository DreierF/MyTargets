/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.statistics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.targets.drawable.TargetImpactAggregationDrawable;
import de.dreier.mytargets.shared.targets.drawable.TargetImpactDrawable;

public class DispersionPatternUtils {

    public static void createDispersionPatternImageFile(int size, @NonNull File f, @NonNull ArrowStatistic statistic) throws FileNotFoundException {
        Bitmap b = getDispersionPatternBitmap(size, statistic);
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

    public static Bitmap getDispersionPatternBitmap(int size, @NonNull ArrowStatistic statistic) {
        Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.drawColor(Color.WHITE);

        TargetImpactDrawable target = new TargetImpactAggregationDrawable(statistic.target);
        target.setShots(statistic.shots);
        target.setArrowDiameter(statistic.arrowDiameter,
                SettingsManager.getInputArrowDiameterScale());
        target.setBounds(new Rect(0, 0, size, size));
        target.draw(canvas);
        return b;
    }
}
