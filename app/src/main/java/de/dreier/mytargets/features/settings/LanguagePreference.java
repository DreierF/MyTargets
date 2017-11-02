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

package de.dreier.mytargets.features.settings;

import android.content.Context;
import android.support.v7.preference.ListPreference;
import android.util.AttributeSet;

import im.delight.android.languages.LanguageList;

/**
 * Preference that extends ListPreference and can be used in XML (or Java) to offer a custom language selection
 */
public class LanguagePreference extends ListPreference {

    public LanguagePreference(Context context) {
        super(context);
        init();
    }

    public LanguagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // use the list of human-readable language names for the displayed list
        setEntries(LanguageList.getHumanReadable());
        // use the list of machine-readable language names for the saved values
        setEntryValues(LanguageList.getMachineReadable());
        // use an empty language code (no custom language) as the default
        setDefaultValue("");
        // set the summary to be auto-updated to the selected value
        setSummary("%s");
    }
}
