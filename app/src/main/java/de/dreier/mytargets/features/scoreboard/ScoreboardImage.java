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

package de.dreier.mytargets.features.scoreboard;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.utils.Utils;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ScoreboardImage {

    private static final int PAGE_WIDTH = 600;
    private static final int MARGIN = 50;

    public void generateBitmap(@NonNull final Activity context, final Training training, final long round, @NonNull final File f) throws IOException {
        final View content = HtmlUtils
                .getScoreboardView(context, Utils
                        .getCurrentLocale(context), training, round, ScoreboardConfiguration
                        .fromShareSettings());

        float density = context.getResources().getDisplayMetrics().density;
        int pageWidth = (int) (PAGE_WIDTH * density);
        int margin = (int) (MARGIN * density);

        content.measure(pageWidth - 2 * margin, WRAP_CONTENT);
        int width = content.getMeasuredWidth();
        int height = content.getMeasuredHeight();
        content.layout(0, 0, width, height);

        Bitmap b = Bitmap
                .createBitmap(width + 2 * margin, height + 2 * margin, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(b);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        canvas.save();
        canvas.translate(margin, margin);
        content.draw(canvas);
        canvas.restore();

        OutputStream fOut = new FileOutputStream(f);
        b.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
        fOut.flush();
        fOut.close();
        b.recycle();
    }
}
