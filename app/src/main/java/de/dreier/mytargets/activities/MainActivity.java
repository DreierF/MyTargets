/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.activities;

import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.Arrays;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.EditTrainingFragment;
import de.dreier.mytargets.fragments.MainFragment;
import de.dreier.mytargets.utils.TranslationUtils;

/**
 * Shows an overview over all training days
 */
public class MainActivity extends SimpleFragmentActivityBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_CustomToolbar);
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

            ShortcutInfo shortcut1 = new ShortcutInfo.Builder(this, "free_training")
                    .setShortLabel(getString(R.string.free_training))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_app_shortcut_trending_up_blue_24px))
                    .setIntent(EditTrainingFragment
                            .createIntent(this, EditTrainingFragment.FREE_TRAINING)
                            .build())
                    .build();

            ShortcutInfo shortcut2 = new ShortcutInfo.Builder(this, "standard_round")
                    .setShortLabel(getString(R.string.training_with_standard_round))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_app_shortcut_album_blue_24px))
                    .setIntent(EditTrainingFragment
                            .createIntent(this, EditTrainingFragment.TRAINING_WITH_STANDARD_ROUND)
                            .build())
                    .build();

            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut1, shortcut2));
        }

        TranslationUtils.askForHelpTranslating(this);
    }

    @Override
    public Fragment instantiateFragment() {
        return new MainFragment();
    }

}
