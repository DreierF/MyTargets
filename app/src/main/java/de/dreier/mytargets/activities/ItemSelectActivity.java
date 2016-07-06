/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import de.dreier.mytargets.fragments.ArrowFragment;
import de.dreier.mytargets.fragments.BowFragment;
import de.dreier.mytargets.fragments.DistanceFragment;
import de.dreier.mytargets.fragments.EnvironmentFragment;
import de.dreier.mytargets.fragments.FragmentBase;
import de.dreier.mytargets.fragments.TargetFragment;
import de.dreier.mytargets.fragments.WindDirectionFragment;
import de.dreier.mytargets.fragments.WindSpeedFragment;

public abstract class ItemSelectActivity extends SimpleFragmentActivityBase
        implements FragmentBase.OnItemSelectedListener {

    public static final String ITEM = "item";
    public static final String INTENT = "intent";

    @Override
    public void onItemSelected(Parcelable item) {
        Intent data = new Intent();
        data.putExtra(ITEM, item);
        data.putExtra(INTENT, getIntent() != null ? getIntent().getExtras() : null);
        setResult(RESULT_OK, data);
        onBackPressed();
    }

    public static class ArrowActivity extends ItemSelectActivity {

        @Override
        public Fragment instantiateFragment() {
            return new ArrowFragment();
        }
    }

    public static class BowActivity extends ItemSelectActivity {

        @Override
        public Fragment instantiateFragment() {
            return new BowFragment();
        }
    }

    public static class DistanceActivity extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new DistanceFragment();
        }
    }

    public static class EnvironmentActivity extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new EnvironmentFragment();
        }
    }

    public static class TargetActivity extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new TargetFragment();
        }
    }

    public static class WindDirectionActivity extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new WindDirectionFragment();
        }
    }

    public static class WindSpeedActivity extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new WindSpeedFragment();
        }
    }
}
