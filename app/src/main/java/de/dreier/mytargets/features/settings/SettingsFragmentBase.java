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

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.ToolbarUtils;

public abstract class SettingsFragmentBase extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String rootKey;

    @Override
    public final void onCreatePreferences(Bundle bundle, String rootKey) {
        this.rootKey = rootKey;
        onCreatePreferences();
    }

    protected void onCreatePreferences() {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        updateItemSummaries();
    }

    @SuppressLint("PrivateResource")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the default white background in the view so as to avoid transparency
        view.setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.background_material_light));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        updateItemSummaries();
    }

    protected void updateItemSummaries() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarUtils.showHomeAsUp(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationInstance.getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    protected void setActivityTitle() {
        if (rootKey == null) {
            getActivity().setTitle(R.string.preferences);
        } else {
            getActivity().setTitle(findPreference(rootKey).getTitle());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ApplicationInstance.getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    protected void setSummary(String key, String value) {
        findPreference(key).setSummary(value);
    }

    public void onFragmentResume() {
        setActivityTitle();
    }
}