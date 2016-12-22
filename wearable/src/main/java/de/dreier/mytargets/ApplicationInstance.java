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
import de.dreier.mytargets.shared.analysis.aggregation.average.Average;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.models.WindDirection;
import de.dreier.mytargets.shared.models.WindSpeed;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.ArrowImage;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.BowImage;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.EndImage;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.SightMark;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.EndRenderer;

/**
 * Application singleton. Gets instantiated exactly once and is used
 * throughout the app whenever a context is needed e.g. to query app
 * resources.
 */
@ParcelClasses({
        @ParcelClass(Average.class),
        @ParcelClass(Arrow.class),
        @ParcelClass(ArrowImage.class),
        @ParcelClass(Bow.class),
        @ParcelClass(BowImage.class),
        @ParcelClass(Dimension.class),
        @ParcelClass(Environment.class),
        @ParcelClass(End.class),
        @ParcelClass(EndImage.class),
        @ParcelClass(EndRenderer.class),
        @ParcelClass(Round.class),
        @ParcelClass(RoundTemplate.class),
        @ParcelClass(Shot.class),
        @ParcelClass(SightMark.class),
        @ParcelClass(StandardRound.class),
        @ParcelClass(NotificationInfo.class),
        @ParcelClass(Target.class),
        @ParcelClass(Training.class),
        @ParcelClass(Thumbnail.class),
        @ParcelClass(WindDirection.class),
        @ParcelClass(WindSpeed.class)
})
public class ApplicationInstance extends SharedApplicationInstance {

}
