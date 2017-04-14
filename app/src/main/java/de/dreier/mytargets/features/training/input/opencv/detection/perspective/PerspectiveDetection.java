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

package de.dreier.mytargets.features.training.input.opencv.detection.perspective;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.features.training.input.opencv.ColorUtils;
import de.dreier.mytargets.features.training.input.opencv.MainActivity;
import de.dreier.mytargets.features.training.input.opencv.transformation.PerspectiveTransformation;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.zone.CircularZone;

import static de.dreier.mytargets.features.training.input.opencv.ColorSegmentationUtils.findBiggestMatchingContour;

public class PerspectiveDetection {

    public PerspectiveTransformation detectPerspective(Mat image, Target target) {
        Mat temp = new Mat();
        removeNoise(image, temp);

        List<CircularZone> distinctColorZones = ColorUtils.getDistinctColorTargetZones(target);
        List<Pair<Float, RotatedRect>> values = getEllipsisForZones(temp, distinctColorZones);
        Point center = calculateLinearRegressionForRadius(values, 0);
        if (center == null) {
            return null;
        }

        RotatedRect outer = extendToFullTarget(values);

        if (MainActivity.DEBUG_STEP == MainActivity.DebugStep.PERSPECTIVE) {
            Imgproc.circle(image, center, 5, new Scalar(0, 255, 255));
            for (Pair<Float, RotatedRect> value : values) {
                Imgproc.ellipse(image, value.second, new Scalar(255, 0, 0), 1, 8);
            }
            Imgproc.ellipse(image, outer, new Scalar(255, 0, 0), 2, 8);
        }

        return new PerspectiveTransformation(outer, center);
    }

    /**
     * Detects each of the given zones by its color.
     *
     * @param image              The image to detect zones on
     * @param distinctColorZones List of zones with distinct colors
     * @return A list with the detected ellipsis packed into a pair.
     * The Float holds the radius of the circular zone (0f-1f) relative to the target faces total size.
     * The RotatedRect describes the detected ellipse.
     */
    @NonNull
    private List<Pair<Float, RotatedRect>> getEllipsisForZones(Mat image, List<CircularZone> distinctColorZones) {
        int i1 = image.width() / 6;
        int i2 = image.height() / 6;
        Rect imageMainContent = new Rect(i1, i2, i1 * 4, i2 * 4);

        List<Pair<Float, RotatedRect>> values = new ArrayList<>();
        for (CircularZone distinctColorZone : distinctColorZones) {
            Mat mask = ColorUtils.getColorMask(image, distinctColorZone.fillColor, true);
            float approxRadius = image.width() * 0.5f * distinctColorZone.radius;
            double minArea = 0.1 * approxRadius * approxRadius;
            
            RotatedRect rect = findBiggestMatchingContour(mask,
                    (area, centerOfMass) -> area >= minArea
                            && imageMainContent.contains(centerOfMass));
            if (rect == null) {
                continue;
            }
            values.add(new Pair<>(distinctColorZone.radius, rect));
        }
        return values;
    }

    private RotatedRect extendToFullTarget(List<Pair<Float, RotatedRect>> values) {
        Pair<Float, RotatedRect> outerDetected = values.get(0);
        Float outerDetectedRadius = outerDetected.first;
        RotatedRect outerDetectedEllipse = outerDetected.second;

        float targetFullRelativeRadius = 1 / outerDetectedRadius;
        Point centerForFull = calculateLinearRegressionForRadius(values, targetFullRelativeRadius);

        double width = outerDetectedEllipse.size.width * targetFullRelativeRadius;
        double height = outerDetectedEllipse.size.height * targetFullRelativeRadius;

        return new RotatedRect(centerForFull, new Size(width, height), outerDetectedEllipse.angle);
    }

    private void removeNoise(Mat image, Mat temp) {
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
        Imgproc.morphologyEx(image, temp, Imgproc.MORPH_CLOSE, kernel);
    }

    private Point calculateLinearRegressionForRadius(List<Pair<Float, RotatedRect>> values, float radius) {
        int n = values.size();
        if (n < 1) {
            return null;
        }
        double[] x = new double[n];
        double[] y = new double[n];
        double[] z = new double[n];
        // first pass: read in data, compute x bar and y bar
        double sumX = 0.0f;
        double sumY = 0.0f;
        double sumZ = 0.0f;
        for (int i = 0; i < n; i++) {
            x[i] = values.get(i).first;
            y[i] = values.get(i).second.center.x;
            z[i] = values.get(i).second.center.y;
            sumX += x[i];
            sumY += y[i];
            sumZ += z[i];
        }
        double xBar = sumX / n;
        double yBar = sumY / n;
        double zBar = sumZ / n;

        // second pass: compute summary statistics
        double xxBar = 0.0f;
        double xyBar = 0.0f;
        double xzBar = 0.0f;
        for (int i = 0; i < n; i++) {
            xxBar += (x[i] - xBar) * (x[i] - xBar);
            xyBar += (x[i] - xBar) * (y[i] - yBar);
            xzBar += (x[i] - xBar) * (z[i] - zBar);
        }
        double betaY1 = xyBar / xxBar;
        double betaZ1 = xzBar / xxBar;
        double betaY0 = yBar - betaY1 * xBar;
        double betaZ0 = zBar - betaZ1 * xBar;

        return new Point(betaY1 * radius + betaY0, betaZ1 * radius + betaZ0);
    }
}
