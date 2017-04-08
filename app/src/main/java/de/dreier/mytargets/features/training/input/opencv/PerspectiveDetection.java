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

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dreier.mytargets.features.training.input.opencv.detection.arrow.Line;
import de.dreier.mytargets.features.training.input.opencv.detection.target.ColorSegmentationTargetSelectionStrategyBase;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;


class PerspectiveDetection extends ColorSegmentationTargetSelectionStrategyBase implements IPerspectiveDetection {

    @Override
    public Mat detectPerspective(Mat image, Target target) {
        float scale = getScale(image);
        Mat temp = scaleImage(image, scale);
        removeNoise(temp);

        Mat mask = detectBlueTargetZoneViaColor(target, temp);

        RotatedRect rect = findAndDrawCenteredContour(mask);

        extendToFullTarget(target, rect);

        if (rect != null) {
            //Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2RGB);
            reverseScale(rect, scale);
            Imgproc.ellipse(image, rect, new Scalar(255, 0, 0), 2, 8);
        }

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
    private Mat detectBlueTargetZoneViaColor(Target target, Mat image) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);


        Mat mask1 = new Mat(); // blue
        float[] hsvColor = new float[3];
        Color.colorToHSV(target.getSelectableZoneList(0).get(5).zone.fillColor, hsvColor);

        Core.inRange(hsv, new Scalar(95, 140, 100), new Scalar(116, 255, 255), mask1);
        Mat mask2 = new Mat(); // red
        Core.inRange(hsv, new Scalar(160, 115, 190), new Scalar(180, 255, 255), mask2);//hue = value (0-360) / 2 //red does not work for one image
        Mat mask22 = new Mat(); // red
        Core.inRange(hsv, new Scalar(0, 115, 190), new Scalar(15, 255, 255), mask22);
        Mat mask3 = new Mat(); // yellow
        Core.inRange(hsv, new Scalar(27, 102, 190), new Scalar(29, 255, 255), mask3);

        Core.bitwise_or(mask1, mask2, mask1);
        Core.bitwise_or(mask1, mask22, mask1);
        Core.bitwise_or(mask1, mask3, mask1);

        // noise removal
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9, 9));
        Imgproc.morphologyEx(mask1, mask1, Imgproc.MORPH_DILATE, kernel);

        return mask1;
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

    private List<Line> detectLinesWithHough(Mat image) {
        Imgproc.Canny(image, image, 20, 100);
        int threshold = 40;
        int minLineSize = 200;
        int lineGap = 30;
        Mat lines = new Mat();
        Imgproc.HoughLinesP(image, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

        List<Line> list = new ArrayList<>();
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            list.add(new Line(start, end));
        }
        return list;
    }
}
