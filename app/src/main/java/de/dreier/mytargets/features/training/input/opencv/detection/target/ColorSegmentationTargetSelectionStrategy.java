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
import de.dreier.mytargets.features.training.input.opencv.transformation.PerspectiveTransformation2;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;

public class ColorSegmentationTargetSelectionStrategy implements ITargetDetectionStrategy {

    @Override
    public ITransformation detectTargetFace(Mat image, Target target) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

        List<SelectableZone> zones = target.getSelectableZoneList(0);

        // Get ellipses for target round

        Mat bin1 = ColorSegmentationUtils.segmentationForColor(hsv, new Scalar(95, 140, 160), new Scalar(105, 255, 255));
        // (133,55%,63%)-(148,100%,100%) blue
        Mat bin2 = ColorSegmentationUtils.segmentationForColor(hsv, new Scalar(170, 115, 190), new Scalar(180, 180, 255));
        // (241,45%,75%)-(256,71%,100%)
        Mat bin3 = ColorSegmentationUtils.segmentationForColor(hsv, new Scalar(27, 102, 190), new Scalar(29, 255, 255));
        // (39,40%,75%)-(40,100%,100%)

        //RotatedRect rotatedRect1 = findAndDrawCenteredContour(bin1);
        RotatedRect rotatedRect1 = ColorSegmentationUtils.findAndDrawCenteredContour(bin2);
        RotatedRect rotatedRect3 = ColorSegmentationUtils.findAndDrawCenteredContour(bin3);

        return new PerspectiveTransformation2(rotatedRect1, rotatedRect3);
    }
}
