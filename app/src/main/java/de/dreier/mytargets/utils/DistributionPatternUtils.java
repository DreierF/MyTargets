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

package de.dreier.mytargets.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.statistics.ArrowStatistic;
import de.dreier.mytargets.shared.targets.drawable.TargetImpactAggregationDrawable;
import de.dreier.mytargets.shared.targets.drawable.TargetImpactDrawable;

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
