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

import de.dreier.mytargets.features.training.input.opencv.transformation.ITransformation;
import de.dreier.mytargets.shared.models.Target;

public interface ITargetDetectionStrategy {
    /**
     * Detects the boundaries of the target face on the given image.
     * @param image
     * @param target
     * @return A transformation object.
     */
    ITransformation detectTargetFace(Mat image, Target target);
}
