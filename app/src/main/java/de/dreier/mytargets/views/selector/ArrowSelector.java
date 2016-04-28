/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;

public class ArrowSelector extends ImageSelectorBase<Arrow> {

    public ArrowSelector(Context context) {
        this(context, null);
    }

    public ArrowSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickActivity(ItemSelectActivity.ArrowActivity.class);
        setAddButtonIntent(SimpleFragmentActivity.EditArrowActivity.class, (data) -> setItemId(0));
    }

    public void setItemId(long arrow) {
        Arrow item = null;
        ArrowDataSource arrowDataSource = new ArrowDataSource(getContext());
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
