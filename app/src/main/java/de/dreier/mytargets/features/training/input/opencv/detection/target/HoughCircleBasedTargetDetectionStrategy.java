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

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import de.dreier.mytargets.features.training.input.opencv.transformation.ITransformation;
import de.dreier.mytargets.shared.models.Target;

public class HoughCircleBasedTargetDetectionStrategy implements ITargetDetectionStrategy {

    @Override
    public ITransformation detectTargetFace(Mat image, Target target) {
        Mat bin1 = new Mat();
        Imgproc.cvtColor(image, bin1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(bin1, bin1, 90, 100);
        int iCannyUpperThreshold = 100;
        int iMinRadius = 20;
        int iMaxRadius = 400;
        int iAccumulator = 300;

        Mat circles = new Mat();
        Imgproc.HoughCircles(bin1, circles, Imgproc.CV_HOUGH_GRADIENT,
                2.0, bin1.rows() / 8, iCannyUpperThreshold, iAccumulator,
                iMinRadius, iMaxRadius);

        if (circles.cols() > 0) {
            for (int x = 0; x < circles.cols(); x++) {
                double vCircle[] = circles.get(0, x);

                if (vCircle == null) {
                    break;
                }

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int) Math.round(vCircle[2]);

                // draw the found circle
                Imgproc.circle(image, pt, radius, new Scalar(0, 255, 0), 2);
                Imgproc.circle(image, pt, 3, new Scalar(0, 0, 255), 2);
            }
        }
        return null;
    }
}
