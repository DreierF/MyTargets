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

import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static de.dreier.mytargets.features.training.input.opencv.ColorSegmentationUtils.findAndDrawCenteredContour;
import static de.dreier.mytargets.shared.utils.Color.BLACK;
import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class PerspectiveCorrection implements IPerspectiveCorrection {

    @Override
    public Mat detectPerspective(Mat image, Target target) {
        float scale = getScale(image);
        Mat temp = scaleImage(image, scale);
        removeNoise(temp);

        List<CircularZone> distinctColorZones = getDistinctColorTargetZones(target);
        List<Pair<Float, RotatedRect>> values = getEllipsisForZones(temp, distinctColorZones);

        // Scale up the ellipsis to its original size
        for (Pair<Float, RotatedRect> value : values) {
            reverseScale(value.second, scale);
//            Imgproc.ellipse(image, value.second, new Scalar(255, 0, 0), 2, 8);
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
        Rect outerBounds = ellipseBoundingRect(outer);

        //Imgproc.ellipse(image, outer, new Scalar(255, 0, 0), 2, 8);

        MatOfPoint2f src = new MatOfPoint2f(
                new Point(outerBounds.x, outerBounds.y + outerBounds.height * 0.5f),
                new Point(center.x, outerBounds.y),
                new Point(center.x, outerBounds.y + outerBounds.height),
                new Point(
                        outerBounds.x + outerBounds.width,
                        outerBounds.y + outerBounds.height * 0.5f));

        double size = image.size().width;
        MatOfPoint2f dst = new MatOfPoint2f(
                new Point(0, size / 2.0f),
                new Point(size / 2.0f, 0),
                new Point(size / 2.0, size),
                new Point(size, size / 2.0));

        //Imgproc.rectangle(image, outerBounds.tl(), outerBounds.br(), new Scalar(255, 0, 0));

        Mat transform = Imgproc.getPerspectiveTransform(src, dst);
        Imgproc.warpPerspective(image, image, transform, new Size(size, size));

        return image;
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
            Mat mask = detectCircularZoneViaColor(image, distinctColorZone);
            RotatedRect rect = findAndDrawCenteredContour(mask);
            if (rect == null) {
                continue;
            }
            values.add(new Pair<>(distinctColorZone.radius, rect));
        }
        return values;
    }

    // Ported from here: http://code.opencv.org/issues/3396
    private Rect ellipseBoundingRect(RotatedRect ellipse) {
        double degree = ellipse.angle * 3.1415 / 180;
        double majorAxe = ellipse.size.width / 2;
        double minorAxe = ellipse.size.height / 2;
        double x = ellipse.center.x;
        double y = ellipse.center.y;
        double c_degree = Math.cos(degree);
        double s_degree = Math.sin(degree);
        double t1 = Math.atan(-(majorAxe * s_degree) / (minorAxe * c_degree));
        double c_t1 = Math.cos(t1);
        double s_t1 = Math.sin(t1);
        double w1 = majorAxe * c_t1 * c_degree;
        double w2 = minorAxe * s_t1 * s_degree;
        double maxX = x + w1 - w2;
        double minX = x - w1 + w2;

        t1 = Math.atan((minorAxe * c_degree) / (majorAxe * s_degree));
        c_t1 = Math.cos(t1);
        s_t1 = Math.sin(t1);
        w1 = minorAxe * s_t1 * c_degree;
        w2 = majorAxe * c_t1 * s_degree;
        double maxY = y + w1 + w2;
        double minY = y - w1 - w2;
        if (minY > maxY) {
            double temp = minY;
            minY = maxY;
            maxY = temp;
        }
        if (minX > maxX) {
            double temp = minX;
            minX = maxX;
            maxX = temp;
        }
        return new Rect((int) minX, (int) minY, (int) (maxX - minX + 1), (int) (
                maxY - minY + 1));
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

        Mat mask = new Mat();
        switch (zone.fillColor) {
            case CERULEAN_BLUE:
                Core.inRange(hsv, new Scalar(95, 140, 100), new Scalar(116, 255, 255), mask);
                break;
            case FLAMINGO_RED:
                Core.inRange(hsv, new Scalar(160, 115, 170), new Scalar(180, 255, 255), mask);
                Mat mask2 = new Mat();
                Core.inRange(hsv, new Scalar(0, 115, 170), new Scalar(15, 255, 255), mask2);
                Core.bitwise_or(mask, mask2, mask);
                break;
            case LEMON_YELLOW:
                Core.inRange(hsv, new Scalar(26, 102, 170), new Scalar(30, 255, 255), mask);
                break;
            case BLACK:
                Core.inRange(hsv, new Scalar(0, 0, 0), new Scalar(180, 76.5, 100), mask);
                break;
            default:
                float[] hsvColor = new float[3];
                Color.colorToHSV(zone.fillColor, hsvColor);
                float hue = hsvColor[0] / 2f;
                Core.inRange(hsv, new Scalar(hue - 10, 100, 170), new Scalar(
                        hue + 10, 255, 255), mask);
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
