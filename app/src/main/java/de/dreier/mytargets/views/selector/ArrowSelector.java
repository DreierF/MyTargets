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

import java.util.List;

import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.shared.models.db.Arrow;

public class ArrowSelector extends ImageSelectorBase<Arrow> {

    private static final int ARROW_REQUEST_CODE = 5;
    private static final int ARROW_ADD_REQUEST_CODE = 6;

    public ArrowSelector(Context context) {
        this(context, null);
    }

    public ArrowSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = ItemSelectActivity.ArrowActivity.class;
        addActivity = SimpleFragmentActivityBase.EditArrowActivity.class;
        requestCode = ARROW_REQUEST_CODE;
    }

    @Override
    protected void onAddButtonClicked() {
        fragment.startActivityForResult(getAddIntent(), ARROW_ADD_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ARROW_ADD_REQUEST_CODE) {
            setItemId(0);
        }
    }

    public void setItemId(long arrowId) {
        Arrow item = null;
        if (arrowId > 0) {
            item = Arrow.get(arrowId);
        }
        if (item == null) {
            List<Arrow> all = Arrow.getAll();
            if (all.size() > 0) {
                item = all.get(0);
            }
        }
        setItem(item);
    }
}
