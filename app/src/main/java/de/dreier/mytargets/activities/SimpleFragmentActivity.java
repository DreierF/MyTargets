/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.MenuItem;

import butterknife.ButterKnife;
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

public abstract class SimpleFragmentActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String FRAGMENT_TAG = "fragment";
    Fragment childFragment;

    protected abstract Fragment instantiateFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    int getLayoutResource() {
        return R.layout.layout_frame;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class TimerActivity extends SimpleFragmentActivity {

        @Override
        public Fragment instantiateFragment() {
            return new TimerFragment();
        }

        @Override
        public void onBackPressed() {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    public static class SettingsActivity extends SimpleFragmentActivity implements
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
    }

    public static class TrainingActivity extends SimpleFragmentActivity {

        @Override
        public Fragment instantiateFragment() {
            return new TrainingFragment();
        }

        @Override
        public void onBackPressed() {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    public static class EditTrainingActivity extends SimpleFragmentActivity {

        @Override
        protected Fragment instantiateFragment() {
            return new EditTrainingFragment();
        }

        @Override
        public void onBackPressed() {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    public static class EditRoundActivity extends SimpleFragmentActivity {

        @Override
        protected Fragment instantiateFragment() {
            return new EditRoundFragment();
        }

        @Override
        public void onBackPressed() {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    public static class EditStandardRoundActivity extends SimpleFragmentActivity {

        @Override
        protected Fragment instantiateFragment() {
            return new EditStandardRoundFragment();
        }

        @Override
        public void onBackPressed() {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    public static class EditBowActivity extends SimpleFragmentActivity {

        @Override
        protected Fragment instantiateFragment() {
            return new EditBowFragment();
        }

        @Override
        public void onBackPressed() {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    public static class EditArrowActivity extends SimpleFragmentActivity {

        @Override
        protected Fragment instantiateFragment() {
            return new EditArrowFragment();
        }

        @Override
        public void onBackPressed() {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    public static class LicencesActivity extends SimpleFragmentActivity {

        @Override
        protected Fragment instantiateFragment() {
            return new LicencesFragment();
        }

    }

    public static class AboutActivity extends SimpleFragmentActivity {

        @Override
        protected Fragment instantiateFragment() {
            return new AboutFragment();
        }

    }
}