/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.ArrowFragment;
import de.dreier.mytargets.fragments.BowFragment;
import de.dreier.mytargets.fragments.DistanceFragment;
import de.dreier.mytargets.fragments.EnvironmentFragment;
import de.dreier.mytargets.fragments.FragmentBase;
import de.dreier.mytargets.fragments.TargetFragment;
import de.dreier.mytargets.fragments.WindDirectionFragment;
import de.dreier.mytargets.fragments.WindSpeedFragment;

public abstract class ItemSelectActivity extends SimpleFragmentActivity
        implements FragmentBase.OnItemSelectedListener,
        FragmentBase.ContentListener {
    public static final String ITEM = "item";

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Nullable
    @Bind(R.id.new_layout)
    View mNewLayout;

    @Bind(R.id.new_text)
    TextView mNewText;

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_frame_fab;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (childFragment instanceof View.OnClickListener) {
            mFab.setOnClickListener(((View.OnClickListener) childFragment)::onClick);
        }
        onContentChanged(true, 0);
    }

    @Override
    public void onContentChanged(boolean empty, int stringRes) {
        if (stringRes != 0 && mNewText != null && mNewLayout != null) {
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
    public void onItemSelected(Parcelable item) {
        Intent data = new Intent();
        data.putExtra(ITEM, item);
        setResult(RESULT_OK, data);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
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
