/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.DynamicItemHolder;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.ArrowNumber;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import icepick.State;

public class EditArrowFragment extends EditWithImageFragmentBase {

    static final String ARROW_ID = "arrow_id";

    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.arrow_length)
    EditText length;
    @Bind(R.id.arrow_material)
    EditText material;
    @Bind(R.id.arrow_spine)
    EditText spine;
    @Bind(R.id.arrow_weight)
    EditText weight;
    @Bind(R.id.arrow_tip_weight)
    EditText tipWeight;
    @Bind(R.id.arrow_vanes)
    EditText vanes;
    @Bind(R.id.arrow_nock)
    EditText nock;
    @Bind(R.id.arrow_comment)
    EditText comment;
    @Bind(R.id.arrowNumbers)
    RecyclerView arrowNumbers;

    @State(ParcelsBundler.class)
    List<ArrowNumber> arrowNumbersList = new ArrayList<>();

    private ArrowNumbersAdapter adapter;

    private long arrowId = -1;

    public EditArrowFragment() {
        super(R.layout.fragment_edit_arrow, R.drawable.arrows);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ARROW_ID)) {
            arrowId = bundle.getLong(ARROW_ID, -1);
        }

        if (savedInstanceState == null) {
            if (arrowId != -1) {
                // Load data from database
                Arrow arrow = new ArrowDataSource().get(arrowId);
                setArrowValues(arrow);
            } else {
                // Set to default values
                setImageFile(null);
                setTitle(R.string.my_arrow);
                name.setText(R.string.my_arrow);
            }
        }

        loadImage(imageFile);
        adapter = new ArrowNumbersAdapter(this, arrowNumbersList);
        arrowNumbers.setAdapter(adapter);
        return rootView;
    }

    @OnClick(R.id.addButton)
    public void onAddSightSetting() {
        arrowNumbersList.add(new ArrowNumber());
        adapter.notifyItemInserted(arrowNumbersList.size() - 1);
    }

    private void setArrowValues(Arrow arrow) {
        setTitle(arrow.name);
        name.setText(arrow.name);
        length.setText(arrow.length);
        material.setText(arrow.material);
        spine.setText(arrow.spine);
        weight.setText(arrow.weight);
        tipWeight.setText(arrow.tipWeight);
        vanes.setText(arrow.vanes);
        nock.setText(arrow.nock);
        comment.setText(arrow.comment);
        setImageFile(arrow.imageFile);
        arrowNumbersList = arrow.numbers;
    }

    @Override
    public void onSave() {
        super.onSave();
        Arrow arrow = buildArrow();
        if (arrow == null) {
            return;
        }
        new ArrowDataSource().update(arrow);
        getActivity().finish();
    }

    private Arrow buildArrow() {
        Arrow arrow = new Arrow();
        arrow.setId(arrowId);
        arrow.name = name.getText().toString();
        arrow.length = length.getText().toString();
        arrow.material = material.getText().toString();
        arrow.spine = spine.getText().toString();
        arrow.weight = weight.getText().toString();
        arrow.tipWeight = tipWeight.getText().toString();
        arrow.vanes = vanes.getText().toString();
        arrow.nock = nock.getText().toString();
        arrow.comment = comment.getText().toString();
        arrow.imageFile = getImageFile();
        arrow.thumb = getThumbnail();
        arrow.numbers = Stream.of(arrowNumbersList)
                .filter(value -> value != null)
                .collect(Collectors.toList());
        return arrow;
    }

    static class ArrowNumberHolder extends DynamicItemHolder<ArrowNumber> {
        @Bind(R.id.arrowNumber)
        EditText arrowNumber;
        @Bind(R.id.removeArrowNumber)
        ImageButton remove;

        ArrowNumberHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnTextChanged(R.id.arrowNumber)
        public void onTextChanged(CharSequence s) {
            if (s.toString().matches("[0-9]+")) {
                item.number = s.toString();
            } else {
                item.number = null;
            }
        }

        @Override
        public void onBind(ArrowNumber number, int position, Fragment fragment, View.OnClickListener removeListener) {
            item = number;
            arrowNumber.setText(number.number);
            remove.setOnClickListener(removeListener);
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