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
import de.dreier.mytargets.shared.models.db.Bow;

public class BowSelector extends ImageSelectorBase<Bow> {

    private static final int BOW_REQUEST_CODE = 7;
    private static final int BOW_ADD_REQUEST_CODE = 8;

    public BowSelector(Context context) {
        this(context, null);
    }

    public BowSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = ItemSelectActivity.BowActivity.class;
        addActivity = SimpleFragmentActivityBase.EditBowActivity.class;
        requestCode = BOW_REQUEST_CODE;
    }

    @Override
    protected void onAddButtonClicked() {
        fragment.startActivityForResult(getAddIntent(), BOW_ADD_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == BOW_ADD_REQUEST_CODE) {
            setItemId(0);
        }
    }

    public void setItemId(long bow) {
        Bow item = null;
        if (bow > 0) {
            item = Bow.get(bow);
        }
        if (item == null) {
            List<Bow> all = Bow.getAll();
            if (all.size() > 0) {
                item = all.get(0);
            }
        }
        setItem(item);
    }
}
