/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import de.dreier.mytargets.databinding.EditArrowFragmentBinding;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.ArrowNumber;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import icepick.State;

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

            setTitle(arrow.name);
            contentBinding.setArrow(arrow);
        }

        loadImage(imageFile);
        adapter = new ArrowNumbersAdapter(this, arrowNumbersList);
        contentBinding.arrowNumbers.setAdapter(adapter);
        return rootView;
    }

    @OnClick(R.id.addButton)
    public void onAddSightSetting() {
        arrowNumbersList.add(new ArrowNumber());
        adapter.notifyItemInserted(arrowNumbersList.size() - 1);
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
