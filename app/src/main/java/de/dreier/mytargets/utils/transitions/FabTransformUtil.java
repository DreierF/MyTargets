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

package de.dreier.mytargets.utils.transitions;

import android.app.Activity;
import android.view.View;

import de.dreier.mytargets.utils.Utils;

public class FabTransformUtil {
    public static void setup(Activity activity, View root) {
        if (Utils.supportsFabTransform()) {
            FabTransform.setup(activity, root);
        }
    }
}
