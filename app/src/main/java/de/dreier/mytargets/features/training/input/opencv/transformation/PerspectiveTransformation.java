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

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class PerspectiveTransformation implements ITransformation {

    private RotatedRect outerRect;
    private RotatedRect innerRect;

    public PerspectiveTransformation(RotatedRect outerRect, RotatedRect innerRect) {
        this.outerRect = outerRect;
        this.innerRect = innerRect;
    }

    @Override
    public void transform(Mat mRgba) {

        //Imgproc.getRectSubPix(mRgba, outerRect.size, outerRect.center, mRgba); // always extracting from canonical position

        double size = Math.min(outerRect.size.width, outerRect.size.height);

        Point center = innerRect.center;
        MatOfPoint2f src = new MatOfPoint2f(
                center,
                new Point(center.x, center.y - outerRect.size.height / 2f),
                new Point(center.x - outerRect.size.width / 2f, center.y + outerRect.size.height / 2f),
                new Point(outerRect.size.width / 2.0 + center.x, center.y));

        MatOfPoint2f dst = new MatOfPoint2f(
                new Point(size / 2.0, size / 2.0),
                new Point(size / 2.0, 0),
                new Point(0, size),
                new Point(size, size / 2.0));

        Mat transform = Imgproc.getPerspectiveTransform(src, dst);
        Imgproc.warpPerspective(mRgba, mRgba, transform, new Size(size, size));
    }
}
