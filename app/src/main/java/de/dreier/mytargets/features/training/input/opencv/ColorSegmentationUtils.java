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

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

public class ColorSegmentationUtils {

    public static Mat segmentationForColor(Mat hsv, Scalar lowerBound, Scalar upperBound) {
        Mat bin = new Mat();
        Core.inRange(hsv, lowerBound, upperBound, bin);

        // noise removal
        Mat opening = new Mat();
        Mat kernel = new Mat(3, 3, CvType.CV_8UC1);
        Imgproc.morphologyEx(bin, opening, Imgproc.MORPH_OPEN, kernel);
        return bin;
    }

    public static RotatedRect findBiggestMatchingContour(Mat bin1, ContourMatcher contourMatcher) {
        //Find contours of blue region
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(bin1, contours, hierarchy, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        MatOfPoint biggestMatchingContour = null;
        double biggestSize = Integer.MIN_VALUE;
        for (MatOfPoint contour : contours) {
            // Sort out contours that are too small
            double area = Imgproc.contourArea(contour);

            Moments mu = Imgproc.moments(contour);
            Point center = new Point(mu.get_m10() / mu.get_m00(), mu.get_m01() / mu.get_m00());

            // If center of the circle is more or less in the center of the image...
            if (area > biggestSize && contourMatcher.matches(area, center)) {
                biggestSize = area;
                biggestMatchingContour = contour;
            }
        }

        // Calculate convex hull
        if (biggestMatchingContour == null) {
            return null;
        }
        List<Point> pointList = biggestMatchingContour.toList();
        MatOfInt hullInt = new MatOfInt();
        Imgproc.convexHull(biggestMatchingContour, hullInt);
        ArrayList<Point> hullPointList = new ArrayList<>();
        int[] hi = hullInt.toArray();
        for (int j : hi) {
            hullPointList.add(pointList.get(j));
        }

        // Draw convex hull
        MatOfPoint hullPointMat = new MatOfPoint();
        MatOfPoint2f hullPointMat2 = new MatOfPoint2f();
        hullPointMat2.fromList(hullPointList);
        hullPointMat.fromList(hullPointList);
        return Imgproc.fitEllipse(hullPointMat2);
    }
}
