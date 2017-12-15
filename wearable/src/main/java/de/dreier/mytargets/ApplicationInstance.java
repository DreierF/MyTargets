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

package de.dreier.mytargets;

import org.parceler.ParcelClass;
import org.parceler.ParcelClasses;

import de.dreier.mytargets.shared.SharedApplicationInstance;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.EndRenderer;
import de.dreier.mytargets.utils.WearWearableClient;

/**
 * Application singleton. Gets instantiated exactly once and is used
 * throughout the app whenever a context is needed e.g. to query app
 * resources.
 */
@ParcelClasses({
        @ParcelClass(EndRenderer.class),
        @ParcelClass(Target.class)
})
public class ApplicationInstance extends SharedApplicationInstance {

    public static WearWearableClient wearableClient;

    @Override
    public void onCreate() {
        super.onCreate();
        wearableClient = new WearWearableClient(this);
    }

    @Override
    public void onTerminate() {
        wearableClient.disconnect();
        super.onTerminate();
    }
}
