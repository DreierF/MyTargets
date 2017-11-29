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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.print.PageRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.features.scoreboard.builder.ViewBuilder;
import de.dreier.mytargets.features.scoreboard.layout.DefaultScoreboardLayout;
import de.dreier.mytargets.features.scoreboard.pdf.ViewPrintDocumentAdapter;
import de.dreier.mytargets.features.scoreboard.pdf.ViewToPdfWriter;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ScoreboardUtils {

    private static final int PAGE_WIDTH = 600;
    private static final int MARGIN = 50;

    public static LinearLayout getScoreboardView(Context context, Locale locale, Training training, long roundId, @NonNull ScoreboardConfiguration configuration) {
        List<Round> rounds;
        if (roundId == -1) {
            rounds = training.getRounds();
        } else {
            rounds = Collections.singletonList(Round.get(roundId));
        }

        DefaultScoreboardLayout scoreboardLayout = new DefaultScoreboardLayout(context, locale, configuration);
        ViewBuilder viewBuilder = new ViewBuilder(context);
        scoreboardLayout.generateWithBuilder(viewBuilder, training, rounds);
        return viewBuilder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void generatePdf(LinearLayout content, File file) throws IOException {
        ViewToPdfWriter writer = new ViewToPdfWriter(content);
        writer.layoutPages(ViewPrintDocumentAdapter.DEFAULT_RESOLUTION, ViewPrintDocumentAdapter.DEFAULT_MEDIA_SIZE);

        OutputStream fileOutputStream = new FileOutputStream(file);
        writer.writePdfDocument(new PageRange[]{PageRange.ALL_PAGES}, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static void generateBitmap(@NonNull Context context, @NonNull LinearLayout content, @NonNull File file) throws IOException {
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

        OutputStream fileOutputStream = new FileOutputStream(file);
        b.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        b.recycle();
    }
}
