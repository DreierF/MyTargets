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

import de.dreier.mytargets.fragments.AboutFragment;
import de.dreier.mytargets.fragments.EditArrowFragment;
import de.dreier.mytargets.fragments.EditBowFragment;
import de.dreier.mytargets.fragments.EditRoundFragment;
import de.dreier.mytargets.fragments.EditStandardRoundFragment;
import de.dreier.mytargets.fragments.EditTrainingFragment;
import de.dreier.mytargets.fragments.LicencesFragment;
import de.dreier.mytargets.fragments.RoundFragment;
import de.dreier.mytargets.fragments.TimerFragment;
import de.dreier.mytargets.fragments.TrainingFragment;
import de.dreier.mytargets.utils.Utils;

public abstract class SimpleFragmentActivityBase extends ChildActivityBase {

    private static final String FRAGMENT_TAG = "fragment";
    Fragment childFragment;

    protected abstract Fragment instantiateFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            ft.replace(android.R.id.content, childFragment, FRAGMENT_TAG);
            ft.commit();
        }
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

    public static class RoundActivity extends SimpleFragmentActivityBase {

        @Override
        public Fragment instantiateFragment() {
            return new RoundFragment();
        }

    }

    public static class EditTrainingActivity extends SimpleFragmentActivityBase {

        @Override
        protected Fragment instantiateFragment() {
            return new EditTrainingFragment();
        }

        @Override
        public void onBackPressed() {
            // Workaround: When cancelling a new training don't animate
            // back to the fab, because clans FAB breaks the transition
            if (Utils.isLollipop()) {
                getWindow().setSharedElementReturnTransition(null);
                getWindow().setSharedElementReenterTransition(null);
            }
            finish();
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