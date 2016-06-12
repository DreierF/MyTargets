/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.dreier.mytargets.R;

public abstract class EditFragmentBase extends Fragment {
    AppCompatActivity activity;

    void setUpToolbar(Toolbar toolbar) {
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        setHasOptionsMenu(true);
    }

    void setTitle(@StringRes int title) {
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setTitle(title);
    }

    void setTitle(String title) {
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setTitle(title);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            getActivity().setResult(Activity.RESULT_OK);
            onSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract void onSave();
}