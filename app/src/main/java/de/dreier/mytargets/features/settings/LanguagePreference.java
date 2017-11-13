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
import android.support.v4.util.Pair;
import android.support.v7.preference.ListPreference;
import android.util.AttributeSet;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Arrays;
import java.util.List;

import im.delight.android.languages.LanguageList;

/**
 * Preference that extends ListPreference and can be used in XML (or Java) to offer a custom language selection
 */
public class LanguagePreference extends ListPreference {

    private static List<String> SUPPORTED_LOCALES = Arrays.asList(
            "",
            "ca",
            "zh-CN",
            "zh-TW",
            "cs",
            "da",
            "nl",
            "et",
            "en",
            "fi",
            "fr",
            "de",
            "he",
            "hu",
            "id",
            "it",
            "ja",
            "no",
            "pl",
            "pt-PT",
            "pt-BR",
            "ru",
            "sr",
            "sk",
            "sl",
            "es",
            "sv",
            "tr",
            "uk"
    );

    public LanguagePreference(Context context) {
        super(context);
        init();
    }

    public LanguagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        List<Pair<String, String>> pairList = Stream
                .zip(Arrays.asList(LanguageList.getHumanReadable()).iterator(), Arrays
                        .asList(LanguageList.getMachineReadable()).iterator(), Pair::new)
                .filter(pair -> SUPPORTED_LOCALES.contains(pair.second))
                .collect(Collectors.toList());

        // use the list of human-readable language names for the displayed list
        setEntries(Stream.of(pairList).map(p -> p.first).toArray(String[]::new));
        // use the list of machine-readable language names for the saved values
        setEntryValues(Stream.of(pairList).map(p -> p.second).toArray(String[]::new));
        // use an empty language code (no custom language) as the default
        setDefaultValue("");
        // set the summary to be auto-updated to the selected value
        setSummary("%s");
    }
}
