/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.adapters.DynamicItemHolder;
import de.dreier.mytargets.databinding.EditArrowFragmentBinding;
import de.dreier.mytargets.databinding.ItemArrowNumberBinding;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.ArrowNumber;
import de.dreier.mytargets.shared.models.Dimension;
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
    private ArrowNumbersAdapter adapter;

    public EditArrowFragment() {
        super(R.drawable.arrows);
    }

    @NonNull
    protected static IntentWrapper createIntent(Fragment fragment) {
        return new IntentWrapper(fragment, SimpleFragmentActivityBase.EditArrowActivity.class);
    }

    @NonNull
    static IntentWrapper editIntent(Fragment fragment, Arrow arrow) {
        Intent i = new Intent(fragment.getContext(),
                SimpleFragmentActivityBase.EditArrowActivity.class);
        i.putExtra(ARROW_ID, arrow.getId());
        return new IntentWrapper(fragment, i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        contentBinding = EditArrowFragmentBinding.inflate(inflater, binding.content, true);
        contentBinding.addButton.setOnClickListener((view) -> onAddArrow());

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey(ARROW_ID)) {
                long arrowId = bundle.getLong(ARROW_ID);
                arrow = new ArrowDataSource().get(arrowId);
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
        adapter = new ArrowNumbersAdapter(this, arrow.numbers);
        contentBinding.arrowNumbers.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        arrow = buildArrow();
        super.onSaveInstanceState(outState);
    }

    private void onAddArrow() {
        arrow.numbers.add(new ArrowNumber());
        final int newItemPosition = arrow.numbers.size() - 1;
        adapter.notifyItemInserted(newItemPosition);
        contentBinding.arrowNumbers.post(
                () -> {
                    binding.content.fullScroll(View.FOCUS_DOWN);
                    contentBinding.arrowNumbers
                            .findViewHolderForAdapterPosition(newItemPosition)
                            .itemView
                            .requestFocus();
                }
        );
    }

    @Override
    public void onSave() {
        super.onSave();
        new ArrowDataSource().update(buildArrow());
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
        arrow.thumb = getThumbnail();
        arrow.numbers = new ArrayList<>(Stream.of(arrow.numbers)
                .filter(value -> value != null)
                .collect(Collectors.toList()));
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

    static class ArrowNumberHolder extends DynamicItemHolder<ArrowNumber> {
        private final ItemArrowNumberBinding binding;

        ArrowNumberHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
            binding.arrowNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    if (s.toString().matches("[0-9]+")) {
                        item.number = s.toString();
                    } else {
                        item.number = null;
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        @Override
        public void onBind(ArrowNumber number, int position, Fragment fragment, View.OnClickListener removeListener) {
            item = number;
            binding.arrowNumber.setText(number.number);
            binding.removeArrowNumber.setOnClickListener(removeListener);
        }
    }

    private class ArrowNumbersAdapter extends DynamicItemAdapter<ArrowNumber> {
        ArrowNumbersAdapter(Fragment fragment, List<ArrowNumber> list) {
            super(fragment, list, R.string.arrow_number_removed);
        }

        @Override
        public DynamicItemHolder<ArrowNumber> onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.item_arrow_number, parent, false);
            return new ArrowNumberHolder(v);
        }
    }
}
