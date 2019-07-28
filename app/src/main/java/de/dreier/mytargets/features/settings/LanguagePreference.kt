/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.features.settings

import android.content.Context
import androidx.preference.ListPreference
import android.util.AttributeSet
import im.delight.android.languages.LanguageList

/**
 * Preference that extends ListPreference and can be used in XML (or Java) to offer a custom language selection
 */
class LanguagePreference(context: Context, attrs: AttributeSet?) : ListPreference(context, attrs) {

    init {
        val pairList = LanguageList.getHumanReadable().zip(LanguageList.getMachineReadable())
            .filter { pair -> SUPPORTED_LOCALES.contains(pair.component2()) }
            .toList()

        // use the list of human-readable language names for the displayed list
        entries = pairList.map { it.component1() }.toTypedArray()
        // use the list of machine-readable language names for the saved values
        entryValues = pairList.map { it.component2() }.toTypedArray()
        // use an empty language code (no custom language) as the default
        setDefaultValue("")
        // set the summary to be auto-updated to the selected value
        summary = "%s"
    }

    companion object {
        private val SUPPORTED_LOCALES = setOf(
            "",
            "ar",
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
            "in",
            "it",
            "ja",
            "no",
            "pl",
            "pt-PT",
            "pt-BR",
            "ro",
            "ru",
            "sr",
            "sk",
            "sl",
            "es",
            "sv",
            "tr",
            "uk"
        )
    }
}
