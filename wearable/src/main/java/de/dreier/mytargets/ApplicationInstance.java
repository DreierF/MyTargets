/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets;

import org.parceler.ParcelClass;
import org.parceler.ParcelClasses;

import de.dreier.mytargets.shared.SharedApplicationInstance;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.ArrowNumber;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.SightSetting;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.models.WindDirection;
import de.dreier.mytargets.shared.models.WindSpeed;
import de.dreier.mytargets.shared.utils.EndRenderer;

/**
 * Application singleton. Gets instantiated exactly once and is used
 * throughout the app whenever a context is needed e.g. to query app
 * resources.
 */
@ParcelClasses({
        @ParcelClass(Arrow.class),
        @ParcelClass(ArrowNumber.class),
        @ParcelClass(Bow.class),
        @ParcelClass(Coordinate.class),
        @ParcelClass(Dimension.class),
        @ParcelClass(Environment.class),
        @ParcelClass(Passe.class),
        @ParcelClass(EndRenderer.class),
        @ParcelClass(Round.class),
        @ParcelClass(RoundTemplate.class),
        @ParcelClass(Shot.class),
        @ParcelClass(SightSetting.class),
        @ParcelClass(StandardRound.class),
        @ParcelClass(NotificationInfo.class),
        @ParcelClass(Target.class),
        @ParcelClass(Training.class),
        @ParcelClass(WindDirection.class),
        @ParcelClass(WindSpeed.class)
})
public class ApplicationInstance extends SharedApplicationInstance {

}