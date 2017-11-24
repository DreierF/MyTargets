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

package de.dreier.mytargets.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.transitions.FabTransform;

public class IntentWrapper {

    @NonNull
    private final Intent intent;
    private final Class<?> intentTargetClass;
    @Nullable
    private Bundle options = null;
    private Integer requestCode;
    private boolean animate = true;
    private Fragment fragment;
    @Nullable
    private Activity activity;

    public IntentWrapper(Class<?> cls) {
        intentTargetClass = cls;
        intent = new Intent();
    }

    @NonNull
    public IntentWrapper withContext(@NonNull Fragment fragment) {
        this.fragment = fragment;
        activity = fragment.getActivity();
        intent.setClass(fragment.getContext(), intentTargetClass);
        return this;
    }

    @NonNull
    public IntentWrapper withContext(@NonNull Activity activity) {
        this.activity = activity;
        intent.setClass(activity, intentTargetClass);
        return this;
    }

    @NonNull
    public IntentWrapper with(String key, long value) {
        intent.putExtra(key, value);
        return this;
    }

    @NonNull
    public IntentWrapper with(String key, int value) {
        intent.putExtra(key, value);
        return this;
    }

    @NonNull
    public IntentWrapper with(String key, boolean value) {
        intent.putExtra(key, value);
        return this;
    }

    @NonNull
    public IntentWrapper with(String key, String value) {
        intent.putExtra(key, value);
        return this;
    }

    @NonNull
    public IntentWrapper with(String key, Parcelable value) {
        intent.putExtra(key, value);
        return this;
    }

    @NonNull
    public IntentWrapper with(String key, long[] values) {
        intent.putExtra(key, values);
        return this;
    }

    @NonNull
    public IntentWrapper action(String action) {
        intent.setAction(action);
        return this;
    }

    @NonNull
    public IntentWrapper fromFab(@NonNull View fab) {
        return fromFab(fab, R.color.colorAccent, R.drawable.ic_add_white_24dp);
    }

    @NonNull
    public IntentWrapper fromFab(@NonNull View fab, @ColorRes int color, int icon) {
        if (Utils.supportsFabTransform()) {
            fab.setTransitionName(fab.getContext().getString(R.string.transition_root_view));
            FabTransform.addExtras(intent, color, icon);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(getActivity(fab), fab,
                            fab.getContext().getString(R.string.transition_root_view));
            this.options = options.toBundle();
        }
        return this;
    }

    private Activity getActivity(@NonNull View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    @NonNull
    public IntentWrapper forResult(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    @NonNull
    public IntentWrapper noAnimation() {
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        animate = false;
        return this;
    }

    @NonNull
    public IntentWrapper clearTopSingleTop() {
        intent.addFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return this;
    }

    public void start() {
        if (fragment == null) {
            start(activity);
        } else {
            start(fragment);
        }
        animate(activity);
    }

    private void start(@NonNull Fragment fragment) {
        if (requestCode == null) {
            fragment.startActivity(intent, options);
        } else {
            fragment.startActivityForResult(intent, requestCode, options);
        }
    }

    private void start(@NonNull Activity activity) {
        if (Utils.isLollipop()) {
            if (requestCode == null) {
                activity.startActivity(intent, options);
            } else {
                activity.startActivityForResult(intent, requestCode, options);
            }
        } else {
            if (requestCode == null) {
                activity.startActivity(intent);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
        }
    }

    private void animate(@NonNull Activity activity) {
        if (!Utils.isLollipop() && animate) {
            activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    @NonNull
    public Intent build() {
        return intent;
    }
}
