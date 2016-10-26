/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

            ShortcutInfo shortcut1 = new ShortcutInfo.Builder(this, "free_training")
                    .setShortLabel(getString(R.string.free_training))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_app_shortcut_trending_up_blue_24px))
                    .setIntent(EditTrainingFragment
                            .createIntent(EditTrainingFragment.FREE_TRAINING)
                            .withContext(this)
                            .build())
                    .build();

            ShortcutInfo shortcut2 = new ShortcutInfo.Builder(this, "standard_round")
                    .setShortLabel(getString(R.string.training_with_standard_round))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_app_shortcut_album_blue_24px))
                    .setIntent(EditTrainingFragment
                            .createIntent(EditTrainingFragment.TRAINING_WITH_STANDARD_ROUND)
                            .withContext(this)
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
