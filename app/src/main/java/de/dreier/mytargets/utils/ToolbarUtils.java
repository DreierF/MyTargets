/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import de.dreier.mytargets.R;


public class ToolbarUtils {
    public static int getActionBarSize(Context context) {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = context.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    public static int getStatusBarSize(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void showUpAsX(Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        ActionBar supportActionBar = activity.getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    public static void showUpArrow(Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
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
        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setTitle(title);
    }

    public static void setTitle(Fragment fragment, String title) {
        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setTitle(title);
    }
}
