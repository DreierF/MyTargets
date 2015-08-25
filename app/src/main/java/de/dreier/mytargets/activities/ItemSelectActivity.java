/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.ArrowFragment;
import de.dreier.mytargets.fragments.BowFragment;
import de.dreier.mytargets.fragments.DistanceFragment;
import de.dreier.mytargets.fragments.EnvironmentFragment;
import de.dreier.mytargets.fragments.NowListFragment;
import de.dreier.mytargets.fragments.NowListFragmentBase;
import de.dreier.mytargets.fragments.StandardRoundFragment;
import de.dreier.mytargets.fragments.TargetFragment;
import de.dreier.mytargets.fragments.WindDirectionFragment;
import de.dreier.mytargets.fragments.WindSpeedFragment;
import de.dreier.mytargets.shared.models.IIdProvider;

public abstract class ItemSelectActivity extends SimpleFragmentActivity
        implements NowListFragment.OnItemSelectedListener,
        NowListFragmentBase.ContentListener {
    public static final String ITEM = "item";

    private FloatingActionButton mFab;
    private View mNewLayout;
    private TextView mNewText;

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_frame_fab;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        if (this instanceof View.OnClickListener) {
            mFab.setOnClickListener((View.OnClickListener) this);
            mNewLayout = findViewById(R.id.new_layout);
            mNewText = (TextView) findViewById(R.id.new_text);
        }
        onContentChanged(true, 0);
    }

    @Override
    public void onContentChanged(boolean empty, int stringRes) {
        if (stringRes != 0 && mNewText != null) {
            mNewLayout.setVisibility(empty ? View.VISIBLE : View.GONE);
            mNewText.setText(stringRes);
        }
        if (this instanceof View.OnClickListener) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(IIdProvider e) {
        Intent data = new Intent();
        data.putExtra(ITEM, (Serializable) e);
        setResult(RESULT_OK, data);
        onBackPressed();
    }

    public static class Bow extends ItemSelectActivity implements View.OnClickListener {

        @Override
        public Fragment instantiateFragment() {
            return new BowFragment();
        }

        @Override
        public void onClick(View v) {
            ((BowFragment) childFragment).onClick(v);
        }
    }

    public static class Arrow extends ItemSelectActivity implements View.OnClickListener {

        @Override
        public Fragment instantiateFragment() {
            return new ArrowFragment();
        }

        @Override
        public void onClick(View v) {
            ((ArrowFragment) childFragment).onClick(v);
        }
    }

    public static class Target extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new TargetFragment();
        }
    }

    public static class Distance extends ItemSelectActivity implements View.OnClickListener {
        @Override
        protected Fragment instantiateFragment() {
            return new DistanceFragment();
        }

        @Override
        public void onClick(View v) {
            ((DistanceFragment) childFragment).onClick(v);
        }
    }

    public static class StandardRound extends ItemSelectActivity implements View.OnClickListener {
        @Override
        protected Fragment instantiateFragment() {
            return new StandardRoundFragment();
        }

        @Override
        public void onClick(View v) {
            ((StandardRoundFragment) childFragment).onClick(v);
        }
    }

    public static class Environment extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new EnvironmentFragment();
        }
    }

    public static class WindSpeed extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new WindSpeedFragment();
        }
    }

    public static class WindDirection extends ItemSelectActivity {
        @Override
        protected Fragment instantiateFragment() {
            return new WindDirectionFragment();
        }
    }
}
