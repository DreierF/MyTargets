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

package de.dreier.mytargets.test.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.Description;

import java.util.Locale;

public class InstrumentedTestBase {

    @Rule
    public TestName testName = new TestName() {
        @Override
        protected void starting(Description d) {
            super.starting(d);
            Log.d("TestName", d.getDisplayName());
        }
    };

    protected void setLocale(Locale locale) {
        // here we update locale for date formatter
        Locale.setDefault(locale);
        // here we update locale for app resources
        Resources res = InstrumentationRegistry.getTargetContext().getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
