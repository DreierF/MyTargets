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

package de.dreier.mytargets.features.training.input.opencv.detection.target;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public abstract class ColorSegmentationTargetSelectionStrategyBase {
    private static final String TAG = "ColorSegmentationTarget";

    private static Scalar hsvToRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0:
                return rgbToString(value, t, p);
            case 1:
                return rgbToString(q, value, p);
            case 2:
                return rgbToString(p, value, t);
            case 3:
                return rgbToString(p, q, value);
            case 4:
                return rgbToString(t, p, value);
            case 5:
                return rgbToString(value, p, q);
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
    }

    private static Scalar rgbToString(float r, float g, float b) {
        return new Scalar(r * 255, g * 255, b * 255);
    }

    protected Mat segmentationForColor(Mat hsv, Scalar lowerBound, Scalar upperBound) {
        Mat bin = new Mat();
        Core.inRange(hsv, lowerBound, upperBound, bin);

        Log.d(TAG, "segmentationForColor: " + lowerBound + " " + hsvToRgb(lowerBound));

        // noise removal
        Mat opening = new Mat();
        Mat kernel = new Mat(3, 3, CvType.CV_8UC1);
        Imgproc.morphologyEx(bin, opening, Imgproc.MORPH_OPEN, kernel);

        // Enable to make detection more robust
//        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
//        Imgproc.dilate(bin, bin, element);
//        Imgproc.dilate(bin, bin, element);
//        Imgproc.erode(bin, bin, new Mat(), new Point(-1, -1), 5);

        return bin;
    }

    private Scalar hsvToRgb(Scalar lowerBound) {
        return hsvToRgb((float) lowerBound.val[0] / 255f, (float) lowerBound.val[1] / 255f, (float) lowerBound.val[2] / 255f);
    }

    protected RotatedRect findAndDrawCenteredContour(Mat bin1) {
        //Find contours of blue region
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(bin1, contours, hierarchy, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        int i1 = bin1.width() / 6;
        int i2 = bin1.height() / 6;
        Rect rect = new Rect(i1, i2, i1*4, i2*4);
        //Imgproc.rectangle(drawing, new Point(i1, i2), new Point(2 * i1, 2 * i2),
        //        new Scalar(0, 255, 0));

        ArrayList<Point> pointList = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            // Sort out contours that are too small
            double area = Imgproc.contourArea(contour);
            Log.d(TAG, "findAndDrawCenteredContour: " + area);
            if (area < 10000) {
                continue;
            }

            // Fit contour in a circle
            Point center = new Point();
            float[] radius = new float[1];
            MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
            List<Point> points = contour.toList();
            matOfPoint2f.fromList(points);
            Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);

            // If center of the circle is more or less in the center of the image...
            if (center.inside(rect)) {
                pointList.addAll(points);
            }
        }
        Log.d(TAG, "findAndDrawCenteredContour: pointList.size()="+pointList.size());
        // Calculate convex hull
        if (pointList.size() > 0) {
            MatOfInt hullInt = new MatOfInt();
            MatOfPoint pointMat = new MatOfPoint();
            pointMat.fromList(pointList);
            Imgproc.convexHull(pointMat, hullInt);
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
//        List<MatOfPoint> hullPoints = new ArrayList<>();
//        hullPoints.add(hullPointMat);
//        Scalar color = new Scalar(0, 255, 0);
//        Imgproc.drawContours(drawing, hullPoints, -1, color, 1);
        return null;
    }
}
