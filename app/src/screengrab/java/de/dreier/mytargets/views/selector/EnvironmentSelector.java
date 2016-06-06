/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;

public class EnvironmentSelector extends ImageSelectorBase<Environment> {

    private static final int ENVIRONMENT_REQUEST_CODE = 9;

    public EnvironmentSelector(Context context) {
        this(context, null);
    }

    public EnvironmentSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTitle(R.string.environment);
        defaultActivity = ItemSelectActivity.EnvironmentActivity.class;
        requestCode = ENVIRONMENT_REQUEST_CODE;
    }

    public void queryWeather(Fragment fragment, int request_code) {
        setDefaultWeather();
    }

    public void onPermissionResult(Activity activity, int[] grantResult) {
        setDefaultWeather();
    }

    private void setDefaultWeather() {
        setItem(new Environment(EWeather.SUNNY, 0, 0));
    }

    @Override
    public Environment getSelectedItem() {
        if (item == null) {
            return new Environment(EWeather.SUNNY, 0, 0);
        }
        return item;
    }
}
