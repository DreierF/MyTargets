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

package de.dreier.mytargets.views.selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.List;

import de.dreier.mytargets.features.arrows.ArrowListActivity;
import de.dreier.mytargets.features.arrows.EditArrowFragment;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.utils.IntentWrapper;

public class ArrowSelector extends ImageSelectorBase<Arrow> {

    private static final int ARROW_REQUEST_CODE = 5;
    private static final int ARROW_ADD_REQUEST_CODE = 6;

    public ArrowSelector(Context context) {
        this(context, null);
    }

    public ArrowSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = ArrowListActivity.class;
        requestCode = ARROW_REQUEST_CODE;
    }

    @NonNull
    @Override
    protected IntentWrapper getAddIntent() {
        return EditArrowFragment.createIntent()
                .forResult(ARROW_ADD_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ARROW_ADD_REQUEST_CODE) {
            setItemId(null);
        }
    }

    public void setItemId(@Nullable Long arrowId) {
        Arrow item = null;
        if (arrowId != null) {
            item = Arrow.Companion.get(arrowId);
        }
        if (item == null) {
            List<Arrow> all = Arrow.Companion.getAll();
            if (all.size() > 0) {
                item = all.get(0);
            }
        }
        setItem(item);
    }
}
