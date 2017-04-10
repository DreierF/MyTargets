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
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import de.dreier.mytargets.features.training.input.opencv.ColorSegmentationUtils;
import de.dreier.mytargets.features.training.input.opencv.transformation.ITransformation;
import de.dreier.mytargets.features.training.input.opencv.transformation.SimpleAffineTransformation;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;

public class SimpleColorSegmentationTargetSelectionStrategy implements ITargetDetectionStrategy {

    @Override
    public ITransformation detectTargetFace(Mat image, Target target) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

        List<SelectableZone> zones = target.getSelectableZoneList(0);

        // Get ellipses for target round

        Mat bin1 = ColorSegmentationUtils.segmentationForColor(hsv, new Scalar(95, 140, 160), new Scalar(105, 255, 255));

        RotatedRect rotatedRect1 = ColorSegmentationUtils.findAndDrawCenteredContour(bin1);

        return new SimpleAffineTransformation(rotatedRect1);
    }

}
