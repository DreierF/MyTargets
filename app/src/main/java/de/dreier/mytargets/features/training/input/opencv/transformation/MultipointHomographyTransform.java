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

package de.dreier.mytargets.features.training.input.opencv.transformation;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MultipointHomographyTransform implements ITransformation {

    @Override
    public void transform(Mat image) {
       // doHomographyTransform(image, rotatedRects.get(0),rotatedRects.get(1), rotatedRects.get(2));
    }

    private void doHomographyTransform(Mat mRgba, RotatedRect rotatedRect1, RotatedRect rotatedRect2, RotatedRect rotatedRect3) {
        double angle = 0;
        angle += rotatedRect1.angle;
        //angle += rotatedRect2.angle;
        angle += rotatedRect3.angle;
        angle = angle / 2.0;

        // Derive a homography from the ellipses
        MatOfPoint2f foundEllipses = getPointsForEllipses(angle, rotatedRect1, rotatedRect2, rotatedRect3);

        MatOfPoint2f template = getPointsForTemplate(angle, new float[]{500, 333, 167});

        Mat homography = Calib3d.findHomography(
                foundEllipses, template, Calib3d.RANSAC, 5);
        double size = Math.min(mRgba.width(), mRgba.height());
        Imgproc.warpPerspective(mRgba, mRgba, homography, new Size(size, size));
    }

    private MatOfPoint2f getPointsForTemplate(double angle, float[] floats) {
        MatOfPoint2f template = new MatOfPoint2f();
        List<Point> templatePoints = new ArrayList<>();
        for (float radius : floats) {
            Point center = new Point(radius, radius);
            Size size = new Size(500, 500);
            RotatedRect rotatedRect = new RotatedRect(center, size, 0);
            addPointsForEllipse(templatePoints, rotatedRect, angle);
        }
        template.fromList(templatePoints);
        return template;
    }

    private MatOfPoint2f getPointsForEllipses(double angle, RotatedRect... rects) {
        MatOfPoint2f template = new MatOfPoint2f();
        List<Point> ellipsePoints = new ArrayList<>();
        for (RotatedRect rotatedRect : rects) {
            addPointsForEllipse(ellipsePoints, rotatedRect, angle);
        }
        template.fromList(ellipsePoints);
        return template;
    }

    private void addPointsForEllipse(List<Point> points, RotatedRect rotatedRect, double angle) {
        Size axes = rotatedRect.size;
        double size_a = axes.width;
        double size_b = axes.height;
        size_a = Math.abs(size_a);
        size_b = Math.abs(size_b);
        int delta = 15;

        double cx = rotatedRect.center.x;
        double cy = rotatedRect.center.y;
        Point prevPt = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

        double alpha = Math.cos(angle * Math.PI / 180.0);
        double beta = Math.sin(angle * Math.PI / 180.0);

        for (int i = 0; i < 360 + delta; i += delta) {
            double x, y;
            angle = i;
            if (angle > 360) {
                angle = 360;
            }
            if (angle < 0) {
                angle += 360;
            }

            x = size_a * Math.cos(angle);
            y = size_b * Math.sin(angle);
            Point pt = new Point();
            pt.x = Math.round(cx + x * alpha - y * beta);
            pt.y = Math.round(cy + x * beta + y * alpha);
            if (pt != prevPt) {
                points.add(pt);
                prevPt = pt;
            }
        }
    }
}
