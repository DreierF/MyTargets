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

public class PerspectiveTransformation2 implements ITransformation {

    private RotatedRect outerRect;
    private RotatedRect innerRect;

    public PerspectiveTransformation2(RotatedRect outerRect, RotatedRect innerRect) {
        this.outerRect = outerRect;
        this.innerRect = innerRect;
    }

    @Override
    public void transform(Mat mRgba) {

        Imgproc.getRectSubPix(mRgba, outerRect.size, outerRect.center, mRgba); // always extracting from canonical position

        double size = Math.min(outerRect.size.width, outerRect.size.height);

        Point centerInner = innerRect.center;
        Point centerOuter = outerRect.center;
        MatOfPoint2f src = new MatOfPoint2f(
                new Point(0, 0),
                new Point(0, outerRect.size.height),
                new Point(outerRect.size.width / 2f + centerInner.x - centerOuter.x, 0),
                new Point(outerRect.size.width / 2f + centerInner.x - centerOuter.x, outerRect.size.height));

        MatOfPoint2f dst = new MatOfPoint2f(
                new Point(0, 0),
                new Point(0, size),
                new Point(size / 2.0, 0),
                new Point(size / 2.0, size));

        Mat transform = Imgproc.getPerspectiveTransform(src, dst);
        Imgproc.warpPerspective(mRgba, mRgba, transform, new Size(size, size));
    }
}
