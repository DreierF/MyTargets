/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import de.dreier.mytargets.adapters.DynamicItemHolder;
import de.dreier.mytargets.databinding.DynamicitemArrowNumbersBinding;
import de.dreier.mytargets.databinding.EditArrowFragmentBinding;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.ArrowNumber;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.utils.ToolbarUtils;
import icepick.State;

import static de.dreier.mytargets.shared.models.Dimension.Unit.INCH;
import static de.dreier.mytargets.shared.models.Dimension.Unit.MILLIMETER;

public class EditArrowFragment extends EditWithImageFragmentBase {

    static final String ARROW_ID = "arrow_id";

    @State(ParcelsBundler.class)
    List<ArrowNumber> arrowNumbersList = new ArrayList<>();

    private EditArrowFragmentBinding contentBinding;
    private ArrowNumbersAdapter adapter;

    private long arrowId = -1;

    public EditArrowFragment() {
        super(R.drawable.arrows);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        contentBinding = EditArrowFragmentBinding.inflate(inflater, binding.content, true);
        contentBinding.addButton.setOnClickListener((view) -> onAddArrow());

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ARROW_ID)) {
            arrowId = bundle.getLong(ARROW_ID, -1);
        }
        if (savedInstanceState == null) {
            Arrow arrow;
            if (arrowId != -1) {
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
            arrowNumbersList = arrow.numbers;
            contentBinding.diameterUnit.setSelection(arrow.diameter.unit == MILLIMETER ? 0 : 1);
        }

        loadImage(imageFile);
        adapter = new ArrowNumbersAdapter(this, arrowNumbersList);
        contentBinding.arrowNumbers.setAdapter(adapter);
        return rootView;
    }

    private void onAddArrow() {
        arrowNumbersList.add(new ArrowNumber());
        final int newItemPosition = arrowNumbersList.size() - 1;
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
        getActivity().finish();
    }

    private Arrow buildArrow() {
        Arrow arrow = new Arrow();
        arrow.setId(arrowId);
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
        arrow.numbers = Stream.of(arrowNumbersList)
                .filter(value -> value != null)
                .collect(Collectors.toList());
        final float diameterValue = Float.parseFloat(contentBinding.diameter.getText().toString());
        final int selectedUnit = contentBinding.diameterUnit.getSelectedItemPosition();
        Dimension.Unit diameterUnit = selectedUnit == 0 ? MILLIMETER : INCH;
        arrow.diameter = new Dimension(diameterValue, diameterUnit);
        return arrow;
    }

    static class ArrowNumberHolder extends DynamicItemHolder<ArrowNumber> {
        private final DynamicitemArrowNumbersBinding binding;

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
            View v = inflater.inflate(R.layout.dynamicitem_arrow_numbers, parent, false);
            return new ArrowNumberHolder(v);
        }
    }
}
