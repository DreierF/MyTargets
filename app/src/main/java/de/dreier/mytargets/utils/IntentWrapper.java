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
import android.support.v4.app.Fragment;
import android.view.View;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.transitions.FabTransform;

public class IntentWrapper {

    private final Intent intent;
    private final Class<?> intentTargetClass;
    private Bundle options = null;
    private Integer requestCode;
    private boolean animate = true;
    private Fragment fragment;
    private Activity activity;

    public IntentWrapper(Class<?> cls) {
        intentTargetClass = cls;
        intent = new Intent();
    }

    public IntentWrapper withContext(Fragment fragment) {
        this.fragment = fragment;
        activity = fragment.getActivity();
        intent.setClass(fragment.getContext(), intentTargetClass);
        return this;
    }

    public IntentWrapper withContext(Activity activity) {
        this.activity = activity;
        intent.setClass(activity, intentTargetClass);
        return this;
    }

    public IntentWrapper with(String key, long value) {
        intent.putExtra(key, value);
        return this;
    }

    public IntentWrapper with(String key, int value) {
        intent.putExtra(key, value);
        return this;
    }

    public IntentWrapper with(String key, boolean value) {
        intent.putExtra(key, value);
        return this;
    }

    public IntentWrapper with(String key, String value) {
        intent.putExtra(key, value);
        return this;
    }

    public IntentWrapper with(String key, Parcelable value) {
        intent.putExtra(key, value);
        return this;
    }

    public IntentWrapper with(String key, long[] values) {
        intent.putExtra(key, values);
        return this;
    }

    public IntentWrapper action(String action) {
        intent.setAction(action);
        return this;
    }

    public IntentWrapper fromFab(View fab) {
        return fromFab(fab, R.color.colorAccent, R.drawable.ic_add_white_24dp);
    }

    public IntentWrapper fromFab(View fab, @ColorRes int color, int icon) {
        if (Utils.isLollipop()) {
            FabTransform.addExtras(intent, color, icon);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(getActivity(fab), fab,
                            fab.getContext().getString(R.string.transition_root_view));
            this.options = options.toBundle();
        }
        return this;
    }

    private Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public IntentWrapper forResult(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public IntentWrapper noAnimation() {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        animate = false;
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

    private void start(Fragment fragment) {
        if (requestCode == null) {
            fragment.startActivity(intent, options);
        } else {
            fragment.startActivityForResult(intent, requestCode, options);
        }
    }

    private void start(Activity activity) {
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

    private void animate(Activity activity) {
        if (!Utils.isLollipop() && animate) {
            activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    public Intent build() {
        return intent;
    }
}
