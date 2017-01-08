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

package de.dreier.mytargets.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import de.dreier.mytargets.features.rounds.EditRoundFragment;
import de.dreier.mytargets.fragments.EditStandardRoundFragment;
import de.dreier.mytargets.features.training.EditTrainingFragment;
import de.dreier.mytargets.features.training.RoundFragment;
import de.dreier.mytargets.features.training.TrainingFragment;
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

    public Fragment getChildFragment() {
        return getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
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

}