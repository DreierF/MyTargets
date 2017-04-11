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

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.features.training.input.opencv.ColorUtils;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static de.dreier.mytargets.features.training.input.opencv.ColorUtils.getZoneMask;

public class ColorTargetDetection implements ITargetDetectionStrategy {

    @Override
    public List<TargetZone> detectTargetFace(Mat image, Target target) {
        Size size = image.size();
        List<TargetZone> list = new ArrayList<>();
        List<SelectableZone> selectableZoneList = target.getSelectableZoneList(0);
        for (int i = selectableZoneList.size() - 2; i >= 0; i--) {
            ZoneBase zoneBase = selectableZoneList.get(i).zone;
            float innerRadius = 0;
            if (i > 0) {
                innerRadius = selectableZoneList.get(i - 1).zone.radius;
            }
            TargetZone zone = new TargetZone();
            zone.model = selectableZoneList.get(i);
            double fullRadius = size.width * 0.5f;
            zone.center = new Point(fullRadius, fullRadius);
            zone.innerRadius = (int) (fullRadius * innerRadius);
            zone.outerRadius = (int) (fullRadius * zone.model.zone.radius);

            Mat mask = ColorUtils.getColorMask(image, zoneBase.fillColor, false);
            Mat maskExpectedPosition = getZoneMask(size, zone, 0);
            Core.bitwise_and(maskExpectedPosition, mask, mask);
            zone.color = Core.mean(image, mask);
            list.add(zone);
        }
        return list;
    }
}
