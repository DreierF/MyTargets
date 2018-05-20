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

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import de.dreier.mytargets.R
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.utils.ToolbarUtils

abstract class SettingsFragmentBase : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var rootKey = "main"

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        this.rootKey = rootKey ?: "main"
        onCreatePreferences()
    }

    protected open fun onCreatePreferences() {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        updateItemSummaries()
    }

    @SuppressLint("PrivateResource")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the default white background in the view so as to avoid transparency
        view.setBackgroundColor(
            ContextCompat.getColor(context!!, R.color.background_material_light)
        )
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        updateItemSummaries()
    }

    protected open fun updateItemSummaries() {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ToolbarUtils.showHomeAsUp(this)
    }

    override fun onResume() {
        super.onResume()
        onFragmentResume()
        SharedApplicationInstance.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    protected open fun setActivityTitle() {
        activity!!.title = findPreference(rootKey).title
    }

    override fun onPause() {
        super.onPause()
        SharedApplicationInstance.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    protected fun setDefaultSummary(key: String) {
        setSummary(key, (findPreference(key) as ListPreference).entry.toString())
    }

    protected fun setSummary(key: String, value: String) {
        findPreference(key).summary = value
    }

    fun onFragmentResume() {
        setActivityTitle()
    }
}
