/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import de.dreier.mytargets.R;


public class ToolbarUtils {

    public static void showUpAsX(Fragment fragment) {
        showUpAsX((AppCompatActivity) fragment.getActivity());
    }

    public static void showUpAsX(AppCompatActivity activity) {
        ActionBar supportActionBar = activity.getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    public static void showHomeAsUp(Fragment fragment) {
        showHomeAsUp((AppCompatActivity) fragment.getActivity());
    }

    public static void showHomeAsUp(AppCompatActivity activity) {
        ActionBar supportActionBar = activity.getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static void setSupportActionBar(Fragment fragment, Toolbar toolbar) {
        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        activity.setSupportActionBar(toolbar);
    }

    public static void setToolbarTransitionName(Toolbar toolbar) {
        TextView textViewTitle = null;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                textViewTitle = (TextView) view;
                break;
            }
        }
        ViewCompat.setTransitionName(textViewTitle, "title");
    }

    public static void setTitle(Fragment fragment, @StringRes int title) {
        setTitle((AppCompatActivity) fragment.getActivity(), title);
    }

    public static void setTitle(Fragment fragment, String title) {
        setTitle((AppCompatActivity) fragment.getActivity(), title);
    }

    public static void setTitle(AppCompatActivity activity, @StringRes int title) {
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setTitle(title);
    }

    public static void setTitle(AppCompatActivity activity, String title) {
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setTitle(title);
    }

    public static void setSubtitle(Fragment fragment, String subtitle) {
        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        setSubtitle(activity, subtitle);
    }

    public static void setSubtitle(AppCompatActivity activity, String subtitle) {
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
}
