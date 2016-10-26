/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import java.util.ArrayList;

import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.fragments.EditArrowFragment;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.utils.IntentWrapper;

public class ArrowSelector extends ImageSelectorBase<Arrow> {

    private static final int ARROW_REQUEST_CODE = 5;
    private static final int ARROW_ADD_REQUEST_CODE = 6;

    public ArrowSelector(Context context) {
        this(context, null);
    }

    public ArrowSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = ItemSelectActivity.ArrowActivity.class;
        requestCode = ARROW_REQUEST_CODE;
    }

    @Override
    protected IntentWrapper getAddIntent() {
        return EditArrowFragment.createIntent()
                .forResult(ARROW_ADD_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ARROW_ADD_REQUEST_CODE) {
            setItemId(0);
        }
    }

    public void setItemId(long arrow) {
        Arrow item = null;
        ArrowDataSource arrowDataSource = new ArrowDataSource();
        if (arrow > 0) {
            item = arrowDataSource.get(arrow);
        }
        if (item == null) {
            ArrayList<Arrow> all = arrowDataSource.getAll();
            if (all.size() > 0) {
                item = all.get(0);
            }
        }
        setItem(item);
    }
}
