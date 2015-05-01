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
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.ArrowFragment;
import de.dreier.mytargets.fragments.BowFragment;
import de.dreier.mytargets.fragments.DistanceFragment;
import de.dreier.mytargets.fragments.NowListFragment;
import de.dreier.mytargets.fragments.PasseFragment;
import de.dreier.mytargets.fragments.SettingsFragment;
import de.dreier.mytargets.fragments.TargetFragment;
import de.dreier.mytargets.fragments.TimerFragment;
import de.dreier.mytargets.models.IdProvider;

public abstract class SimpleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment instantiateFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);
        Fragment childFragment = instantiateFragment();
        Bundle bundle = getIntent() != null ? getIntent().getExtras() : null;
        childFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, childFragment).commit();
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public static class TimerActivity extends SimpleFragmentActivity {

        @Override
        public Fragment instantiateFragment() {
            return new TimerFragment();
        }
    }

    public static class SettingsActivity extends SimpleFragmentActivity {

        @Override
        public Fragment instantiateFragment() {
            return new SettingsFragment();
        }
    }

    public static class TrainingActivity extends SimpleFragmentActivity {

        @Override
        public Fragment instantiateFragment() {
            return new PasseFragment();
        }
    }

    public static abstract class ItemSelectActivity extends SimpleFragmentActivity
            implements NowListFragment.OnItemSelectedListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            int text = getIntent().getIntExtra("title", R.string.app_name);
            setTitle(text);
        }

        @Override
        public void onItemSelected(long itemId, Class<? extends IdProvider> aClass) {
            Intent data = new Intent();
            data.putExtra("id", itemId);
            setResult(RESULT_OK, data);
            onBackPressed();
        }
    }

    public static class BowItemSelectActivity extends ItemSelectActivity {

        @Override
        public Fragment instantiateFragment() {
            return new BowFragment();
        }
    }

    public static class ArrowItemSelectActivity extends ItemSelectActivity {

        @Override
        public Fragment instantiateFragment() {
            return new ArrowFragment();
        }
    }

    public static class TargetItemSelectActivity extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new TargetFragment();
        }
    }

    public static class DistanceItemSelectActivity extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            DistanceFragment fragment = new DistanceFragment();
            fragment.setArguments(getIntent().getExtras());
            return fragment;
        }
    }
}