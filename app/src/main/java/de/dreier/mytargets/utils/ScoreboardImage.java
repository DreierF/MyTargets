package de.dreier.mytargets.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

public class ScoreboardImage {
    private Bitmap b;

    public void generateBitmap(final Activity context, final long mRound, boolean scoreboard, boolean dispersion_pattern, boolean comments, final File f) {

        // Generate html content
        final String content = ScoreboardUtils.getHTMLString(context, mRound, scoreboard, dispersion_pattern, comments);

        final CountDownLatch signal = new CountDownLatch(1);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Attach WebView to activity
                final WebView webView = new WebView(context);
                webView.setVisibility(View.INVISIBLE);
                final FrameLayout container = (FrameLayout) context.findViewById(android.R.id.content);
                ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                webView.setLayoutParams(p);
                container.addView(webView);

                // Render html to bitmap
                webView.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "UTF-8", "");
                webView.setPictureListener(new WebView.PictureListener() {

                    public void onNewPicture(WebView view, Picture picture) {
                        picture = webView.capturePicture();

                        b = BitmapUtils.pictureDrawable2Bitmap(picture);

                        // Write bitmap to stream
                        try {
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
                    }
                });
            }
        });

        // Wait for thread to complete
        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
