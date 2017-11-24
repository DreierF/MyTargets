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

package de.dreier.mytargets.features.arrows;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.fragments.EditWithImageFragmentBase;
import de.dreier.mytargets.databinding.FragmentEditArrowBinding;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.ArrowImage;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import icepick.State;

import static de.dreier.mytargets.shared.models.Dimension.Unit.INCH;
import static de.dreier.mytargets.shared.models.Dimension.Unit.MILLIMETER;
import static java.lang.Integer.parseInt;

public class EditArrowFragment extends EditWithImageFragmentBase<ArrowImage> {

    @VisibleForTesting
    public static final String ARROW_ID = "arrow_id";
    @Nullable
    @State(ParcelsBundler.class)
    Arrow arrow;
    private FragmentEditArrowBinding contentBinding;

    public EditArrowFragment() {
        super(R.drawable.arrows, ArrowImage.class);
    }

    @NonNull
    public static IntentWrapper createIntent() {
        return new IntentWrapper(EditArrowActivity.class);
    }

    @NonNull
    public static IntentWrapper editIntent(long arrowId) {
        return new IntentWrapper(EditArrowActivity.class)
                .with(ARROW_ID, arrowId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        contentBinding = FragmentEditArrowBinding.inflate(inflater, binding.content, true);
        contentBinding.moreFields.setOnClickListener(v -> contentBinding.setShowAll(true));

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey(ARROW_ID)) {
                arrow = Arrow.get(bundle.getLong(ARROW_ID));
            } else {
                // Set to default values
                arrow = new Arrow();
                arrow.name = getString(R.string.my_arrow);
            }

            setImageFiles(arrow.getImages());
            contentBinding.diameterUnit.setSelection(arrow.diameter.unit == MILLIMETER ? 0 : 1);
        }
        ToolbarUtils.setTitle(this, arrow.name);
        contentBinding.setArrow(arrow);
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
        if (!validateInput()) {
            return;
        }
        buildArrow().save();
        finish();
    }

    private boolean validateInput() {
        float diameterValue;
        try {
            diameterValue = Float.parseFloat(contentBinding.diameter.getText().toString());
        } catch (NumberFormatException ignored) {
            contentBinding.diameterTextInputLayout
                    .setError(getString(R.string.invalid_decimal_number));
            return false;
        }
        final int selectedUnit = contentBinding.diameterUnit.getSelectedItemPosition();
        Dimension.Unit diameterUnit = selectedUnit == 0 ? MILLIMETER : INCH;
        if (diameterUnit == MILLIMETER) {
            if (diameterValue < 1 || diameterValue > 20) {
                contentBinding.diameterTextInputLayout
                        .setError(getString(R.string.not_within_expected_range_mm));
                return false;
            }
        } else {
            if (diameterValue < 0 || diameterValue > 1) {
                contentBinding.diameterTextInputLayout
                        .setError(getString(R.string.not_within_expected_range_inch));
                return false;
            }
        }
        contentBinding.diameterTextInputLayout.setError(null);
        return true;
    }

    @Nullable
    private Arrow buildArrow() {
        arrow.name = contentBinding.name.getText().toString();
        arrow.maxArrowNumber = parseInt(contentBinding.maxArrowNumber.getText().toString());
        arrow.length = contentBinding.length.getText().toString();
        arrow.material = contentBinding.material.getText().toString();
        arrow.spine = contentBinding.spine.getText().toString();
        arrow.weight = contentBinding.weight.getText().toString();
        arrow.tipWeight = contentBinding.tipWeight.getText().toString();
        arrow.vanes = contentBinding.vanes.getText().toString();
        arrow.nock = contentBinding.nock.getText().toString();
        arrow.comment = contentBinding.comment.getText().toString();
        arrow.images = getImageFiles();
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
