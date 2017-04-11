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
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dreier.mytargets.features.training.input.opencv.ColorUtils;
import de.dreier.mytargets.features.training.input.opencv.MainActivity;
import de.dreier.mytargets.features.training.input.opencv.transformation.PerspectiveTransformation;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static de.dreier.mytargets.features.training.input.opencv.ColorSegmentationUtils.findAndDrawCenteredContour;
import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;

public class PerspectiveDetection {

    public PerspectiveTransformation detectPerspective(Mat image, Target target) {
        Mat temp = new Mat();
        removeNoise(image, temp);

        List<CircularZone> distinctColorZones = ColorUtils.getDistinctColorTargetZones(target);
        List<Pair<Float, RotatedRect>> values = getEllipsisForZones(temp, distinctColorZones);

        float targetUpscale = 1 / distinctColorZones.get(0).radius;
        Pair<Point, Point> points = generateLinearRegressionLine(values, targetUpscale);
        if (points == null) {
            return null;
        }
        Point center = points.first;
        RotatedRect outer = values.get(0).second;
        extendToFullTarget(target, outer);
        outer.center = points.second;

        if(MainActivity.DEBUG_STEP == MainActivity.DebugStep.PERSPECTIVE) {
            Imgproc.circle(image, center, 5, new Scalar(0, 255, 255));
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
        List<Pair<Float, RotatedRect>> values = new ArrayList<>();
        for (CircularZone distinctColorZone : distinctColorZones) {
            Mat mask = ColorUtils.getColorMask(image, distinctColorZone.fillColor, true);
            RotatedRect rect = findAndDrawCenteredContour(mask);
            if (rect == null) {
                continue;
            }
            values.add(new Pair<>(distinctColorZone.radius, rect));
        }
        return values;
    }

    private void extendToFullTarget(Target target, RotatedRect rect) {
        List<Integer> detectedZoneColors = Arrays.asList(LEMON_YELLOW, FLAMINGO_RED, CERULEAN_BLUE);
        List<SelectableZone> zones = target.getSelectableZoneList(0);
        for (int i = zones.size() - 1; i >= 0; i--) {
            ZoneBase zone = zones.get(i).zone;
            if (detectedZoneColors.contains(zone.fillColor)) {
                float scale = 1 / zone.radius;
                rect.size.width = rect.size.width * scale;
                rect.size.height = rect.size.height * scale;
                return;
            }
        }
    }

    private void removeNoise(Mat image, Mat temp) {
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
        Imgproc.morphologyEx(image, temp, Imgproc.MORPH_CLOSE, kernel);
    }

    private Pair<Point, Point> generateLinearRegressionLine(List<Pair<Float, RotatedRect>> values, float targetUpscale) {
        int n = values.size();
        if (n < 1) {
            return null;
        }
        double[] x = new double[n];
        double[] y1 = new double[n];
        double[] y2 = new double[n];
        // first pass: read in data, compute x bar and y bar
        double sumX = 0.0f;
        double sumY1 = 0.0f;
        double sumY2 = 0.0f;
        for (int i = 0; i < n; i++) {
            x[i] = values.get(i).first;
            y1[i] = values.get(i).second.center.x;
            y2[i] = values.get(i).second.center.y;
            sumX += x[i];
            sumY1 += y1[i];
            sumY2 += y2[i];
        }
        double xBar = sumX / n;
        double y1Bar = sumY1 / n;
        double y2Bar = sumY2 / n;

        // second pass: compute summary statistics
        double xxBar = 0.0f;
        double xy1Bar = 0.0f;
        double xy2Bar = 0.0f;
        for (int i = 0; i < n; i++) {
            xxBar += (x[i] - xBar) * (x[i] - xBar);
            xy1Bar += (x[i] - xBar) * (y1[i] - y1Bar);
            xy2Bar += (x[i] - xBar) * (y2[i] - y2Bar);
        }
        double beta11 = xy1Bar / xxBar;
        double beta12 = xy2Bar / xxBar;
        double beta01 = y1Bar - beta11 * xBar;
        double beta02 = y2Bar - beta12 * xBar;

        return new Pair<>(new Point(beta01, beta02),
                new Point(beta11 * targetUpscale + beta01, beta12 * targetUpscale + beta02));
    }
}
