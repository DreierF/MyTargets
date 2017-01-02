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

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import de.dreier.mytargets.fragments.ArrowListFragment;
import de.dreier.mytargets.fragments.BowListFragment;
import de.dreier.mytargets.fragments.DistanceFragment;
import de.dreier.mytargets.fragments.EnvironmentFragment;
import de.dreier.mytargets.fragments.ListFragmentBase;
import de.dreier.mytargets.fragments.TargetListFragment;
import de.dreier.mytargets.fragments.WindDirectionListFragment;
import de.dreier.mytargets.fragments.WindSpeedListFragment;

public abstract class ItemSelectActivity extends SimpleFragmentActivityBase
        implements ListFragmentBase.OnItemSelectedListener {

    public static final String ITEM = "item";
    public static final String INTENT = "intent";

    @Override
    public void onItemSelected(Parcelable item) {
        Intent data = new Intent();
        data.putExtra(ITEM, item);
        data.putExtra(INTENT, getIntent() != null ? getIntent().getExtras() : null);
        setResult(RESULT_OK, data);
        super.onBackPressed();
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

        @Override
        public void onBackPressed() {
            ((EnvironmentFragment)getChildFragment()).onSave();
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
}
