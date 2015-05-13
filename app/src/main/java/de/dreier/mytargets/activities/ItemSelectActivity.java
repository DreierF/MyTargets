/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.ArrowFragment;
import de.dreier.mytargets.fragments.BowFragment;
import de.dreier.mytargets.fragments.DistanceFragment;
import de.dreier.mytargets.fragments.EnvironmentFragment;
import de.dreier.mytargets.fragments.NowListFragment;
import de.dreier.mytargets.fragments.TargetFragment;
import de.dreier.mytargets.fragments.WindDirectionFragment;
import de.dreier.mytargets.fragments.WindSpeedFragment;
import de.dreier.mytargets.shared.models.IdProvider;

public abstract class ItemSelectActivity extends SimpleFragmentActivity
        implements NowListFragment.OnItemSelectedListener {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int text = getIntent().getIntExtra("title", R.string.app_name);
        setTitle(text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    @Override
    public void onItemSelected(long itemId, Class<? extends IdProvider> aClass) {
        Intent data = new Intent();
        data.putExtra("id", itemId);
        setResult(RESULT_OK, data);
        onBackPressed();
    }

    @Override
    public void onItemSelected(IdProvider e) {
        Intent data = new Intent();
        data.putExtra("item", e);
        setResult(RESULT_OK, data);
        onBackPressed();
    }

    public static class Bow extends ItemSelectActivity {

        @Override
        public Fragment instantiateFragment() {
            return new BowFragment();
        }
    }

    public static class Arrow extends ItemSelectActivity {

        @Override
        public Fragment instantiateFragment() {
            return new ArrowFragment();
        }
    }

    public static class Target extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new TargetFragment();
        }
    }

    public static class Distance extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            DistanceFragment fragment = new DistanceFragment();
            fragment.setArguments(getIntent().getExtras());
            return fragment;
        }
    }

    public static class Environment extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            EnvironmentFragment fragment = new EnvironmentFragment();
            fragment.setArguments(getIntent().getExtras());
            return fragment;
        }
    }

    public static class WindSpeed extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            WindSpeedFragment fragment = new WindSpeedFragment();
            fragment.setArguments(getIntent().getExtras());
            return fragment;
        }
    }

    public static class WindDirection extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            WindDirectionFragment fragment = new WindDirectionFragment();
            fragment.setArguments(getIntent().getExtras());
            return fragment;
        }
    }
}
