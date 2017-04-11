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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import de.dreier.mytargets.features.training.input.opencv.transformation.ITransformation;
import de.dreier.mytargets.shared.models.Target;
import timber.log.Timber;

public class HoughCircleBasedTargetDetectionStrategy implements ITargetDetectionStrategy {

    @Override
    public ITransformation detectTargetFace(Mat image, Target target) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        Imgproc.GaussianBlur(grayImage, grayImage, new Size(9, 9), 2, 2);

        int iCannyUpperThreshold = 100;
        int iMinRadius = (int) (image.size().width * 0.45f);
        int iMaxRadius = (int) (image.size().width * 0.55f);
        int iAccumulator = 300;

        Mat circles = new Mat();
        Imgproc.HoughCircles(grayImage, circles, Imgproc.CV_HOUGH_GRADIENT,
                2.0, grayImage.rows() / 8, iCannyUpperThreshold, iAccumulator,
                iMinRadius, iMaxRadius);

        Timber.d("detectTargetFace: %d", circles.cols());
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
