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

public class SimpleAffineTransformation implements ITransformation {

    private RotatedRect outerTargetRing;

    public SimpleAffineTransformation(RotatedRect outerTargetRing) {
        this.outerTargetRing = outerTargetRing;
    }

    @Override
    public void transform(Mat mRgba) {
        Imgproc.getRectSubPix(mRgba, outerTargetRing.size, outerTargetRing.center, mRgba); // always extracting from canonical position

        double size = Math.min(outerTargetRing.size.width, outerTargetRing.size.height);

        MatOfPoint2f src = new MatOfPoint2f(
                new Point(0, 0),
                new Point(outerTargetRing.size.width, 0),
                new Point(0, outerTargetRing.size.height));

        MatOfPoint2f dst = new MatOfPoint2f(
                new Point(0, 0),
                new Point(size, 0),
                new Point(0, size));

        Mat transform = Imgproc.getAffineTransform(src, dst);
        Imgproc.warpAffine(mRgba, mRgba, transform, new Size(size, size));
    }
}
