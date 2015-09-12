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
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.FragmentBase;
import de.dreier.mytargets.fragments.SelectItemFragment;
import de.dreier.mytargets.shared.models.IIdProvider;

public abstract class ItemSelectActivity extends SimpleFragmentActivity
        implements SelectItemFragment.OnItemSelectedListener,
        FragmentBase.ContentListener {
    public static final String ITEM = "item";

    private FloatingActionButton mFab;
    private View mNewLayout;
    private TextView mNewText;

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_frame_fab;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        if (childFragment instanceof View.OnClickListener) {
            mFab.setOnClickListener(((View.OnClickListener) childFragment)::onClick);
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
    public void onItemSelected(IIdProvider item) {
        Intent data = new Intent();
        data.putExtra(ITEM, (Serializable) item);
        setResult(RESULT_OK, data);
        onBackPressed();
    }
}
