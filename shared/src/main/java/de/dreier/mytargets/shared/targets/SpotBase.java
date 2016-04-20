/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import android.support.annotation.StringRes;

public class SpotBase extends TargetModelBase {
    protected SpotBase(long id, @StringRes int nameRes) {
        super(id, nameRes);
    }
}
