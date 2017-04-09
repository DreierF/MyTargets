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

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.dreier.mytargets.features.training.input.opencv.detection.target.ColorSegmentationTargetSelectionStrategyBase;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;
import timber.log.Timber;

import static de.dreier.mytargets.shared.utils.Color.BLACK;
import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;
import static de.dreier.mytargets.shared.utils.Color.WHITE;


class PerspectiveDetection extends ColorSegmentationTargetSelectionStrategyBase implements IPerspectiveDetection {

    @Override
    public Mat detectPerspective(Mat image, Target target) {
        float scale = getScale(image);
        Mat temp = scaleImage(image, scale);
        removeNoise(temp);

        List<CircularZone> distinctColorZones = getDistinctColorTargetZones(target);
        Timber.d("detectPerspective: " + distinctColorZones);
        List<Pair<Float, RotatedRect>> values = new ArrayList<>();
        for (CircularZone distinctColorZone : distinctColorZones) {
            Mat mask = detectCircularZoneViaColor(temp, distinctColorZone);
            RotatedRect rect = findAndDrawCenteredContour(mask);
            if (rect == null) {
                continue;
            }
            Timber.d("detectPerspective: (%f, %f)", rect.center.x, rect.center.y);
            reverseScale(rect, scale);
            //Imgproc.ellipse(image, rect, new Scalar(255, 0, 0), 2, 8);
            values.add(new Pair<>(distinctColorZone.radius, rect));
        }

        float targetUpscale = 1 / distinctColorZones.get(0).radius;
        Pair<Point, Point> points = generateLinearRegressionLine(values, targetUpscale);
        if (points == null) {
            return image;
        }
        Point center = points.first;
        Imgproc.circle(image, center, 5, new Scalar(0, 255, 255));
        RotatedRect outer = values.get(0).second;
        extendToFullTarget(target, outer);
        outer.center = points.second;
        Rect outerBounds = outer.boundingRect();

        ////Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2RGB);
        //Imgproc.ellipse(image, outer, new Scalar(255, 0, 0), 2, 8);

        MatOfPoint2f src = new MatOfPoint2f(
                new Point(outerBounds.x, outerBounds.y + outerBounds.height * 0.5f),
                new Point(center.x, outerBounds.y),
                new Point(center.x, outerBounds.y + outerBounds.height),
                new Point(outerBounds.x + outerBounds.width, outerBounds.y + outerBounds.height * 0.5f));

        double size = image.size().width;
        MatOfPoint2f dst = new MatOfPoint2f(
                new Point(0, size / 2.0f),
                new Point(size / 2.0f, 0),
                new Point(size / 2.0, size),
                new Point(size, size / 2.0));

        Mat transform = Imgproc.getPerspectiveTransform(src, dst);
        Imgproc.warpPerspective(image, image, transform, new Size(size, size));

//        Imgproc.cvtColor(hsv, hsv, Imgproc.COLOR_HSV2BGR);
//
//        Imgproc.cvtColor(temp, temp, Imgproc.COLOR_BGR2GRAY);
//
//        List<Line> lines = detectLinesWithHough(temp);
//
//        Imgproc.cvtColor(temp, temp, Imgproc.COLOR_GRAY2RGB);
//        for (Line line : lines) {
//            Imgproc.line(temp, line.point1, line.point2, new Scalar(255, 0, 0), 3);
//           // Log.d(TAG, "   found line (" + x1 + "," + y1 + ")-(" + x2 + "," + y2 + ")");
//        }

        return image;
    }

    private void reverseScale(RotatedRect rect, float scale) {
        float revScale = 1 / scale;
        rect.center.x = rect.center.x * revScale;
        rect.center.y = rect.center.y * revScale;
        rect.size.width = rect.size.width * revScale;
        rect.size.height = rect.size.height * revScale;
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

    @NonNull
    private Mat detectCircularZoneViaColor(Mat image, CircularZone zone) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

        float[] hsvColor = new float[3];
        Color.colorToHSV(zone.fillColor, hsvColor);

        Mat mask = new Mat();
        switch (zone.fillColor) {
            case CERULEAN_BLUE:
                Timber.d("detectCircularZoneViaColor detectPerspective: blue");
                Core.inRange(hsv, new Scalar(95, 140, 100), new Scalar(116, 255, 255), mask);
                break;
            case FLAMINGO_RED:
                Timber.d("detectCircularZoneViaColor detectPerspective: red");
                Core.inRange(hsv, new Scalar(160, 115, 170), new Scalar(180, 255, 255), mask);
                Mat mask2 = new Mat();
                Core.inRange(hsv, new Scalar(0, 115, 170), new Scalar(15, 255, 255), mask2);
                Core.bitwise_or(mask, mask2, mask);
                break;
            case LEMON_YELLOW:
                Timber.d("detectCircularZoneViaColor detectPerspective: yellow");
                Core.inRange(hsv, new Scalar(26, 102, 170), new Scalar(30, 255, 255), mask);
                break;
        }

        // noise removal
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_DILATE, kernel);

        return mask;
    }

    private List<CircularZone> getDistinctColorTargetZones(Target target) {
        List<SelectableZone> allZones = target.getSelectableZoneList(0);
        List<CircularZone> distinctZones = new ArrayList<>();
        HashSet<Integer> colors = new HashSet<>();
        colors.add(BLACK);
        colors.add(WHITE);
        for (int i = allZones.size() - 1; i >= 0; i--) {
            SelectableZone zone = allZones.get(i);
            if (!colors.contains(zone.zone.fillColor)) {
                distinctZones.add((CircularZone) zone.zone);
                colors.add(zone.zone.fillColor);
            }
        }
        return distinctZones;
    }

    private void removeNoise(Mat temp) {
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
        Imgproc.morphologyEx(temp, temp, Imgproc.MORPH_CLOSE, kernel);
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
//
//    private List<Line> detectLinesWithHough(Mat image) {
//        Imgproc.Canny(image, image, 20, 100);
//        int threshold = 40;
//        int minLineSize = 200;
//        int lineGap = 30;
//        Mat lines = new Mat();
//        Imgproc.HoughLinesP(image, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);
//
//        List<Line> list = new ArrayList<>();
//        for (int x = 0; x < lines.rows(); x++) {
//            double[] vec = lines.get(x, 0);
//            double x1 = vec[0],
//                    y1 = vec[1],
//                    x2 = vec[2],
//                    y2 = vec[3];
//            Point start = new Point(x1, y1);
//            Point end = new Point(x2, y2);
//            list.add(new Line(start, end));
//        }
//        return list;
//    }

    private Pair<Point, Point> generateLinearRegressionLine(List<Pair<Float, RotatedRect>> values, float targetUpscale) {
        int dataSetSize = values.size();
        double[] x = new double[dataSetSize];
        double[] y1 = new double[dataSetSize];
        double[] y2 = new double[dataSetSize];
        // first pass: read in data, compute x bar and y bar
        int n = 0;
        double sumX = 0.0f;
        double sumY1 = 0.0f;
        double sumY2 = 0.0f;
        for (int i = 0; i < dataSetSize; i++) {
            x[n] = values.get(i).first;
            y1[n] = values.get(i).second.center.x;
            y2[n] = values.get(i).second.center.y;
            sumX += x[n];
            sumY1 += y1[n];
            sumY2 += y2[n];
            n++;
        }
        if (n < 1) {
            return null;
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
