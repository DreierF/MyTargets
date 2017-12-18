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

package de.dreier.mytargets.test.utils.assertions;

import android.support.annotation.NonNull;
import android.support.test.espresso.ViewAssertion;

import de.dreier.mytargets.features.training.input.TargetView;
import de.dreier.mytargets.shared.streamwrapper.Stream;
import de.dreier.mytargets.shared.views.TargetViewBase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TargetViewAssertions {
    public static ViewAssertion virtualButtonNotExists(@NonNull String desc) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            TargetView targetView = (TargetView) view;
            TargetViewBase.VirtualView vv = Stream.of(targetView.getVirtualViews())
                    .filter(virtualView -> virtualView.getDescription().equals(desc))
                    .findFirstOrNull();
            assertNull("Virtual button does exist", vv);
        };
    }

    public static ViewAssertion virtualButtonExists(@NonNull String desc) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            TargetView targetView = (TargetView) view;
            TargetViewBase.VirtualView vv = Stream.of(targetView.getVirtualViews())
                    .filter(virtualView -> virtualView.getDescription().equals(desc))
                    .findFirstOrNull();
            assertNotNull("Virtual button does not exist", vv);
        };
    }
}
