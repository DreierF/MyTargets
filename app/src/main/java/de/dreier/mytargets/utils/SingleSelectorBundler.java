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

package de.dreier.mytargets.utils;

import android.os.Bundle;

import de.dreier.mytargets.utils.multiselector.SingleSelector;
import icepick.Bundler;

public class SingleSelectorBundler implements Bundler<SingleSelector> {
    public void put(String key, SingleSelector value, Bundle bundle) {
        bundle.putBundle(key, value.saveSelectionStates());
    }

    public SingleSelector get(String key, Bundle bundle) {
        SingleSelector selector = new SingleSelector();
        selector.restoreSelectionStates(bundle.getBundle(key));
        return selector;
    }
}