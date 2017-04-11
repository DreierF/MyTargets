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
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ImageManipulationsSurfaceViewBinding;
import de.dreier.mytargets.features.training.input.opencv.detection.arrow.DilationArrowDetection;
import de.dreier.mytargets.features.training.input.opencv.detection.arrow.IArrowDetectionStrategy;
import de.dreier.mytargets.features.training.input.opencv.detection.perspective.PerspectiveDetection;
import de.dreier.mytargets.features.training.input.opencv.detection.target.ColorTargetDetection;
import de.dreier.mytargets.features.training.input.opencv.detection.target.ITargetDetectionStrategy;
import de.dreier.mytargets.features.training.input.opencv.detection.target.TargetZone;
import de.dreier.mytargets.features.training.input.opencv.transformation.PerspectiveTransformation;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.WA6Ring;
import de.dreier.mytargets.shared.targets.models.WAFull;
import timber.log.Timber;

import static de.dreier.mytargets.features.training.input.opencv.MainActivity.DebugStep.ARROWS;
import static de.dreier.mytargets.features.training.input.opencv.MainActivity.DebugStep.PERSPECTIVE;
import static de.dreier.mytargets.features.training.input.opencv.MainActivity.DebugStep.RESULT;
import static de.dreier.mytargets.features.training.input.opencv.MainActivity.DebugStep.TARGET;
import static org.opencv.core.Core.NORM_MINMAX;

public class MainActivity extends Activity {

    public TargetDataSet[] images = new TargetDataSet[]{
            new TargetDataSet(R.drawable.a1_x_noise, WA6Ring.ID, 1),
            new TargetDataSet(R.drawable.a3_886_shaky, WAFull.ID, 3),
            new TargetDataSet(R.drawable.a3_987, WAFull.ID, 3),
            new TargetDataSet(R.drawable.a3_996_noise, WAFull.ID, 3),
            new TargetDataSet(R.drawable.a3_998, WAFull.ID, 3),
            new TargetDataSet(R.drawable.a3_x83, WAFull.ID, 3),
            new TargetDataSet(R.drawable.a3_x86, WAFull.ID, 3),
            new TargetDataSet(R.drawable.a3_x86_front, WAFull.ID, 3),
            new TargetDataSet(R.drawable.a3_x96, WAFull.ID, 3),
            new TargetDataSet(R.drawable.a6_x99765_noise, WAFull.ID, 6),
            new TargetDataSet(R.drawable.a6_x99999_multiple_targets, WA6Ring.ID, 6),
            new TargetDataSet(R.drawable.a6_xxxx99_overlap, WA6Ring.ID, 6),
            new TargetDataSet(R.drawable.a8_xxx99988_front, WA6Ring.ID, 8),
            new TargetDataSet(R.drawable.a8_xxx99988_overlap, WA6Ring.ID, 8)
    };

    int currentIndex = 0;
    private ImageManipulationsSurfaceViewBinding binding;

    public void setDebugStep(DebugStep debugStep) {
        DEBUG_STEP = debugStep;
        updateImage();
    }

    public static DebugStep DEBUG_STEP = RESULT;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = DataBindingUtil
                .setContentView(this, R.layout.image_manipulations_surface_view);

        Timber.uprootAll();
        Timber.plant(new Timber.DebugTree());

        binding.perspective.setOnClickListener(v -> setDebugStep(PERSPECTIVE));
        binding.target.setOnClickListener(v -> setDebugStep(TARGET));
        binding.arrow.setOnClickListener(v -> setDebugStep(ARROWS));
        binding.result.setOnClickListener(v -> setDebugStep(RESULT));

        binding.prev.setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % images.length;
            updateImage();
        });
        binding.next.setOnClickListener(v -> {
            currentIndex = (currentIndex - 1 + images.length) % images.length;
            updateImage();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            finish();
            return;
        }
        updateImage();
    }

    private void updateImage() {
        try {
            TargetDataSet image = images[currentIndex];
            Target target = new Target(image.targetId, 0);
            binding.imageView.setImageBitmap(getBmp(target, image.drawable, image.arrows));
            binding.imageTitle.setText(getResources().getResourceName(image.drawable));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Bitmap getBmp(Target target, int drawable, int arrows) throws IOException {
        Mat image = Utils.loadResource(this, drawable, Imgcodecs.CV_LOAD_IMAGE_COLOR);

        float scale = getScale(image);
        Mat temp = scaleImage(image, scale);
        Core.normalize(temp, temp, 0, 255, NORM_MINMAX);

        PerspectiveDetection perspectiveDetection = new PerspectiveDetection();
        PerspectiveTransformation perspective = perspectiveDetection
                .detectPerspective(temp, target);
        if (perspective == null) {
            throw new RuntimeException("Perspective detection failed!");
        }
        if (DEBUG_STEP == PERSPECTIVE) {
            perspective.drawTransformationToSource(temp);
            Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2BGRA, 4);
            return toBitmap(temp);
        }
        perspective.transform(temp, temp);

// TODO Look again at http://vision.stanford.edu/teaching/cs231a_autumn1213_internal/project/final/writeup/nondistributable/lin_nguyen_paper_arrowsmith_cs231a_final_project.pdf
        // TODO:
        // - improve ranking algorithm (black area size spanned by the lines)
        // - extend detected arrow at the end to get more accurate position
        // - more accurate technique to decide which of the line ends is the arrow head
        // - add a second pass to the perspective detection, which also includes smaller areas at the expected areas
        // - exclude vertical edges at the target border
        // - make target (circle) detection more accurate
        //

        ITargetDetectionStrategy targetDetection = new ColorTargetDetection();
        List<TargetZone> zones = targetDetection.detectTargetFace(temp, target);
        if (DEBUG_STEP == TARGET) {
            for (TargetZone zone : zones) {
                int radius = (int) ((zone.outerRadius - zone.innerRadius) * 0.5f);
                Timber.d("getBmp: %d", radius);
                Point center = new Point(zone.center.x, zone.center.y - zone.innerRadius - radius);
                Imgproc.circle(temp, center, radius, zone.color, Core.FILLED, 8, 0);
                Imgproc.circle(temp, zone.center, zone.outerRadius, new Scalar(255, 0, 255));
            }
            Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2BGRA, 4);
            return toBitmap(temp);
        }

        boolean fromLeftViewpoint = perspective.isFromLeftViewpoint();
        IArrowDetectionStrategy arrowDetectionStrategy = new DilationArrowDetection();
        List<Point> points = arrowDetectionStrategy
                .detectArrows(temp, zones, arrows, fromLeftViewpoint);

        if (DEBUG_STEP == ARROWS) {
            Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2BGRA, 4);
            return toBitmap(temp);
        }

        if (temp.channels() == 1) {
            Imgproc.cvtColor(temp, temp, Imgproc.COLOR_GRAY2BGRA, 4);
        } else {
            Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2BGRA, 4);
        }

        // Draw arrows
        Scalar green = new Scalar(0, 255, 0);
        for (Point arrow : points) {
            Imgproc.circle(temp, arrow, 20, green);
            Timber.d("getBmp: (%f, %f)", arrow.x, arrow.y);
        }

        return toBitmap(temp);
    }

    @NonNull
    private Bitmap toBitmap(Mat temp) {
        Bitmap bmp = Bitmap.createBitmap(temp.width(), temp.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(temp, bmp);
        return bmp;
    }

    @NonNull
    private Mat scaleImage(Mat image, float scale) {
        Size newSize = new Size(image.width() * scale, image.height() * scale);
        Mat temp = new Mat();
        Imgproc.resize(image, temp, newSize);
        return temp;
    }

    private float getScale(Mat image) {
        int maxImageEdge = Math.max(image.width(), image.height());
        return 1000 / (float) maxImageEdge;
    }

    private void reverseScale(RotatedRect rect, float scale) {
        float revScale = 1 / scale;
        rect.center.x = rect.center.x * revScale;
        rect.center.y = rect.center.y * revScale;
        rect.size.width = rect.size.width * revScale;
        rect.size.height = rect.size.height * revScale;
    }

    private class TargetDataSet {
        private final int drawable;
        private final long targetId;
        private final int arrows;

        public TargetDataSet(int drawable, long targetId, int arrows) {
            this.drawable = drawable;
            this.targetId = targetId;
            this.arrows = arrows;
        }
    }

    public enum DebugStep {
        PERSPECTIVE,
        TARGET,
        ARROWS,
        RESULT
    }
}
