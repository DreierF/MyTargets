/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.databinding.EditArrowFragmentBinding;

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import icepick.State;

import static de.dreier.mytargets.shared.models.Dimension.Unit.INCH;
import static de.dreier.mytargets.shared.models.Dimension.Unit.MILLIMETER;

public class EditArrowFragment extends EditWithImageFragmentBase {

    private static final String ARROW_ID = "arrow_id";
    @State(ParcelsBundler.class)
    Arrow arrow;
    private EditArrowFragmentBinding contentBinding;

    public EditArrowFragment() {
        super(R.drawable.arrows);
    }

    @NonNull
    public static IntentWrapper createIntent() {
        return new IntentWrapper(SimpleFragmentActivityBase.EditArrowActivity.class);
    }

    @NonNull
    public static IntentWrapper editIntent(Arrow arrow) {
        return new IntentWrapper(SimpleFragmentActivityBase.EditArrowActivity.class)
                .with(ARROW_ID, arrow.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        contentBinding = EditArrowFragmentBinding.inflate(inflater, binding.content, true);

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey(ARROW_ID)) {
                long arrowId = bundle.getLong(ARROW_ID);
                arrow = Arrow.get(arrowId);
                setImageFile(arrow.imageFile);
            } else {
                // Set to default values
                arrow = new Arrow();
                arrow.name = getString(R.string.my_arrow);
                setImageFile(null);
            }

            ToolbarUtils.setTitle(this, arrow.name);
            contentBinding.setArrow(arrow);
            contentBinding.diameterUnit.setSelection(arrow.diameter.unit == MILLIMETER ? 0 : 1);
        } else {
            contentBinding.setArrow(arrow);
        }

        loadImage(imageFile);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        arrow = buildArrow();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSave() {
        super.onSave();
        buildArrow().save();
        finish();
    }

    private Arrow buildArrow() {
        arrow.name = contentBinding.name.getText().toString();
        arrow.length = contentBinding.length.getText().toString();
        arrow.material = contentBinding.material.getText().toString();
        arrow.spine = contentBinding.spine.getText().toString();
        arrow.weight = contentBinding.weight.getText().toString();
        arrow.tipWeight = contentBinding.tipWeight.getText().toString();
        arrow.vanes = contentBinding.vanes.getText().toString();
        arrow.nock = contentBinding.nock.getText().toString();
        arrow.comment = contentBinding.comment.getText().toString();
        arrow.imageFile = getImageFile();
        arrow.thumbnail = getThumbnail();
        float diameterValue;
        try {
            diameterValue = Float.parseFloat(contentBinding.diameter.getText().toString());
        } catch (NumberFormatException ignored) {
            diameterValue = 5f;
        }
        final int selectedUnit = contentBinding.diameterUnit.getSelectedItemPosition();
        Dimension.Unit diameterUnit = selectedUnit == 0 ? MILLIMETER : INCH;
        arrow.diameter = new Dimension(diameterValue, diameterUnit);
        return arrow;
    }
}
