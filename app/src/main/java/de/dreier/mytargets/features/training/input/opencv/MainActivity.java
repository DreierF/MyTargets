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

package de.dreier.mytargets.features.training.input.opencv;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.WA6Ring;
import de.dreier.mytargets.shared.targets.models.WAFull;
import timber.log.Timber;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    public TargetDataSet[] images = new TargetDataSet[]{
            new TargetDataSet(R.drawable.a1_x_noise, WA6Ring.ID),
            new TargetDataSet(R.drawable.a3_886_shaky, WAFull.ID),
            new TargetDataSet(R.drawable.a3_987, WAFull.ID),
            new TargetDataSet(R.drawable.a3_996_noise, WAFull.ID),
            new TargetDataSet(R.drawable.a3_998, WAFull.ID),
            new TargetDataSet(R.drawable.a3_x83, WAFull.ID),
            new TargetDataSet(R.drawable.a3_x86, WAFull.ID),
            new TargetDataSet(R.drawable.a3_x86_front, WAFull.ID),
            new TargetDataSet(R.drawable.a3_x96, WAFull.ID),
            new TargetDataSet(R.drawable.a6_x99765_noise, WAFull.ID),
            new TargetDataSet(R.drawable.a6_x99999_multiple_targets, WA6Ring.ID),
            new TargetDataSet(R.drawable.a6_xxxx99_overlap, WA6Ring.ID),
            new TargetDataSet(R.drawable.a8_xxx99988_front, WA6Ring.ID),
            new TargetDataSet(R.drawable.a8_xxx99988_overlap, WA6Ring.ID)
    };
    private ImageView img;
    private int currentIndex = 0;
    private TextView imageTitle;

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.image_manipulations_surface_view);

        Button mButton = (Button) findViewById(R.id.button);
        mButton.setVisibility(View.GONE);
        img = (ImageView) findViewById(R.id.imageView);

        imageTitle = (TextView) findViewById(R.id.imageTitle);

        //mButton.setOnClickListener(v -> takePic = true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            finish();
            return;
        }

        img.setOnClickListener(v -> showNextImage());
        showNextImage();
    }

    private void showNextImage() {
        try {
            Target target = new Target(images[currentIndex].targetId, 2);
            img.setImageBitmap(getBmp(target, images[currentIndex].drawable));
            imageTitle.setText(getResources().getResourceName(images[currentIndex].drawable));
            currentIndex = (currentIndex + 1) % images.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBmp(Target target, int drawable) throws IOException {
        Mat mRgba = Utils.loadResource(this, drawable, Imgcodecs.CV_LOAD_IMAGE_COLOR);

        Timber.uprootAll();
        Timber.plant(new Timber.DebugTree());
        IPerspectiveDetection perspectiveDetection = new PerspectiveDetection();
        mRgba = perspectiveDetection.detectPerspective(mRgba, target);

//        ITargetDetectionStrategy targetDetectionStrategy = new ColorSegmentationTargetSelectionStrategy();
//        ITransformation transformation = targetDetectionStrategy.detectTargetFace(mRgba, target);
        // TODO Look again at http://vision.stanford.edu/teaching/cs231a_autumn1213_internal/project/final/writeup/nondistributable/lin_nguyen_paper_arrowsmith_cs231a_final_project.pdf
        // TODO Test with detecting the target boundaries for transformation

        //correctEllipseProportion(targetBounds, target);

//        for (RotatedRect rotatedRect : targetBounds) {
//            Imgproc.ellipse(mRgba, rotatedRect, new Scalar(255, 0, 255));
//        }


//        ITransformation transformationStrategy = new SimpleAffineTransformation();
//        transformationStrategy.transform(mRgba);

//        transformation.transform(mRgba);
//
//        boolean fromLeftViewpoint = true;
//        IArrowDetectionStrategy arrowDetectionStrategy = new DilationArrowDetection();
//        List<Point> points = arrowDetectionStrategy.detectArrows(mRgba, target, 8, fromLeftViewpoint);
//
        if (mRgba.channels() == 1) {
            Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
        } else {
            Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_RGB2BGRA, 4);
        }

//
//        // Draw arrows
//        Scalar green = new Scalar(0, 255, 0);
//        for (Point arrow : points) {
//            Imgproc.circle(mRgba, arrow, 20, green);
//            Log.d("apply", "   found arrow (" + arrow.x + "," + arrow.y + ")");
//        }

        Bitmap bmp = Bitmap.createBitmap(mRgba.width(), mRgba.height(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(mRgba, bmp);
        return bmp;
    }


    private void correctEllipseProportion(List<RotatedRect> rects, Target target) {
        correctPositions(rects);
        correctSize(rects, target);
    }

    private void correctSize(List<RotatedRect> rects, Target target) {
        float[] radius = new float[]{500, 333, 167};
        final double threshold = 0.05;
        double avg = 0;
        for (RotatedRect rect : rects) {
            double prop = rect.size.width / rect.size.height;
            avg += prop;
        }
        avg = avg / rects.size();

        for (int i = 0; i < rects.size(); i++) {
            double prop = rects.get(i).size.width / rects.get(i).size.height;
            int nearest = i - 1;
            if (i == 0) {
                nearest = 1;
            }
            // Ellipse is too wide or too narrow
            if (prop > avg * (1 + threshold) || prop < avg * (1 - threshold)) {
                rects.get(i).size.width = radius[i] * rects.get(nearest).size.width / radius[nearest];
                rects.get(i).size.height = radius[i] * rects.get(nearest).size.height / radius[nearest];
            }
        }
    }

    private void correctPositions(List<RotatedRect> rects) {
        double cx = 0, cy = 0;
        for (RotatedRect rect : rects) {
            cx += rect.center.x;
            cy += rect.center.y;
        }
        cx = cx / rects.size();
        cy = cy / rects.size();

        double threshold2 = rects.get(0).size.width * 3.0 / 100.0;
        for (RotatedRect rect : rects) {
            double x = rect.center.x;
            double y = rect.center.y;
            if (x > cx + threshold2 || x < cx - threshold2) {
                rect.center.x = cx;
            }
            if (y > cy + threshold2 || y < cy - threshold2) {
                rect.center.y = cy;
            }
        }
    }

    private class TargetDataSet {
        private final int drawable;
        private final int targetId;

        public TargetDataSet(int drawable, int targetId) {
            this.drawable = drawable;
            this.targetId = targetId;
        }
    }
}
