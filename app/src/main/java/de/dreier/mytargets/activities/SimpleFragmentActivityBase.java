/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.AboutFragment;
import de.dreier.mytargets.fragments.EditArrowFragment;
import de.dreier.mytargets.fragments.EditBowFragment;
import de.dreier.mytargets.fragments.EditRoundFragment;
import de.dreier.mytargets.fragments.EditStandardRoundFragment;
import de.dreier.mytargets.fragments.EditTrainingFragment;
import de.dreier.mytargets.fragments.LicencesFragment;
import de.dreier.mytargets.fragments.SettingsFragment;
import de.dreier.mytargets.fragments.TimerFragment;
import de.dreier.mytargets.fragments.TrainingFragment;

public abstract class SimpleFragmentActivityBase extends ChildActivityBase {

    private static final String FRAGMENT_TAG = "fragment";
    Fragment childFragment;
    protected ViewDataBinding binding;

    protected abstract Fragment instantiateFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutResource());

        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            childFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            if (childFragment == null) {
                childFragment = instantiateFragment();
                Bundle bundle = getIntent() != null ? getIntent().getExtras() : null;
                childFragment.setArguments(bundle);
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, childFragment, FRAGMENT_TAG);
            ft.commit();
        }
    }

    int getLayoutResource() {
        return R.layout.layout_frame;
    }

    public static class TimerActivity extends SimpleFragmentActivityBase {

        @Override
        public Fragment instantiateFragment() {
            return new TimerFragment();
        }
    }

    public static class TrainingActivity extends SimpleFragmentActivityBase {

        @Override
        public Fragment instantiateFragment() {
            return new TrainingFragment();
        }

    }

    public static class EditTrainingActivity extends SimpleFragmentActivityBase {

        @Override
        protected Fragment instantiateFragment() {
            return new EditTrainingFragment();
        }

    }

    public static class EditRoundActivity extends SimpleFragmentActivityBase {

        @Override
        protected Fragment instantiateFragment() {
            return new EditRoundFragment();
        }

    }

    public static class EditStandardRoundActivity extends SimpleFragmentActivityBase {

        @Override
        protected Fragment instantiateFragment() {
            return new EditStandardRoundFragment();
        }

    }

    public static class EditBowActivity extends SimpleFragmentActivityBase {

        @Override
        protected Fragment instantiateFragment() {
            return new EditBowFragment();
        }

    }

    public static class EditArrowActivity extends SimpleFragmentActivityBase {

        @Override
        protected Fragment instantiateFragment() {
            return new EditArrowFragment();
        }

    }

    public static class SettingsActivity extends SimpleFragmentActivityBase implements
            PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

        @Override
        public Fragment instantiateFragment() {
            return new SettingsFragment();
        }

        @Override
        public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat,
                                               PreferenceScreen preferenceScreen) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            SettingsFragment fragment = new SettingsFragment();
            Bundle args = new Bundle();
            args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
            fragment.setArguments(args);
            ft.add(R.id.content, fragment, preferenceScreen.getKey());
            ft.addToBackStack(preferenceScreen.getKey());
            ft.commit();
            return true;
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            overridePendingTransition(0, 0);
        }
    }

    public static class LicencesActivity extends SimpleFragmentActivityBase {

        @Override
        protected Fragment instantiateFragment() {
            return new LicencesFragment();
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            overridePendingTransition(0, 0);
        }
    }

    public static class AboutActivity extends SimpleFragmentActivityBase {

        @Override
        protected Fragment instantiateFragment() {
            return new AboutFragment();
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            overridePendingTransition(0, 0);
        }
    }
}