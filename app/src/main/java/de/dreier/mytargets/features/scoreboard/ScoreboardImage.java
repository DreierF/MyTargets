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
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import de.dreier.mytargets.shared.utils.BitmapUtils;

public class ScoreboardImage {

    public void generateBitmap(final Activity context, final long training, final long round, final File f) {

        // Generate html content
        final String content = HtmlUtils
                .getScoreboard(training, round, ScoreboardConfiguration.fromShareSettings());

        final CountDownLatch signal = new CountDownLatch(1);
        context.runOnUiThread(() -> {
            // Enable the drawing of the whole document for Lollipop to get the whole WebView
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                WebView.enableSlowWholeDocumentDraw();
            }
            // Attach WebView to activity
            final WebView webView = new WebView(context);
            webView.setVisibility(View.INVISIBLE);
            final FrameLayout container = (FrameLayout) context
                    .findViewById(android.R.id.content);
            ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            webView.setLayoutParams(p);
            container.addView(webView);

            // Render html to bitmap
            webView.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "UTF-8",
                    "");
            webView.setPictureListener((view, picture) -> {
                picture = webView.capturePicture();

                // Write bitmap to stream
                try {
                    Bitmap b = BitmapUtils.pictureDrawable2Bitmap(picture);
                    OutputStream fOut = new FileOutputStream(f);
                    b.compress(Bitmap.CompressFormat.PNG, 50, fOut);
                    fOut.flush();
                    fOut.close();
                    b.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                signal.countDown();

                // Remove WebView from layout
                container.removeView(webView);
            });
        });

        // Wait for thread to complete
        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
