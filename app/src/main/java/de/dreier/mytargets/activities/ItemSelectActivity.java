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

import de.dreier.mytargets.fragments.ArrowListFragment;
import de.dreier.mytargets.fragments.BowListFragment;
import de.dreier.mytargets.fragments.DistanceFragment;
import de.dreier.mytargets.fragments.EnvironmentFragment;
import de.dreier.mytargets.fragments.FragmentBase;
import de.dreier.mytargets.fragments.TargetListFragment;
import de.dreier.mytargets.fragments.WindDirectionListFragment;
import de.dreier.mytargets.fragments.WindSpeedListFragment;
import de.dreier.mytargets.utils.backup.BackupLocationListFragment;

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
            return new ArrowListFragment();
        }
    }

    public static class BowActivity extends ItemSelectActivity {

        @Override
        public Fragment instantiateFragment() {
            return new BowListFragment();
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
            return new TargetListFragment();
        }
    }

    public static class WindDirectionActivity extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new WindDirectionListFragment();
        }
    }

    public static class WindSpeedActivity extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new WindSpeedListFragment();
        }
    }

    public static class BackupLocationActivity extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new BackupLocationListFragment();
        }
    }
}
