/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.ArrowNumber;

public class EditArrowFragment extends EditWithImageFragmentBase {

    public static final String ARROW_ID = "arrow_id";
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
    @Bind(R.id.arrow_numbers)
    EditText arrowNumbers;
    private long mArrowId = -1;

    public EditArrowFragment() {
        super(R.layout.fragment_edit_arrow, R.drawable.arrows);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ARROW_ID)) {
            mArrowId = bundle.getLong(ARROW_ID, -1);
        }

        if (savedInstanceState == null) {
            if (mArrowId != -1) {
                // Load data from database
                Arrow arrow = new ArrowDataSource().get(mArrowId);
                setArrowValues(arrow);
            } else {
                // Set to default values
                loadImage((String) null);
                setTitle(R.string.my_arrow);
                name.setText(R.string.my_arrow);
            }
        } else {
            // Restore values from before orientation change
            Arrow bow = Parcels.unwrap(savedInstanceState.getParcelable("arrow"));
            setArrowValues(bow);
        }

        return rootView;
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
        loadImage(arrow.imageFile);
        String text = "";
        for (ArrowNumber arrowNumber : arrow.numbers) {
            if (!text.isEmpty()) {
                text += ",";
            }
            text += arrowNumber.number;
        }
        arrowNumbers.setText(text);
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
        ArrayList<ArrowNumber> numbers = getArrowNumbers();
        if (numbers == null) {
            return null;
        }
        Arrow arrow = new Arrow();
        arrow.setId(mArrowId);
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
        arrow.numbers = numbers;
        return arrow;
    }

    private ArrayList<ArrowNumber> getArrowNumbers() {
        String text = arrowNumbers.getText().toString().replace(" ", "");
        if (!text.matches("([0-9]*(,[0-9]*)*)?")) {
            arrowNumbers.setError(getString(R.string.not_matches_sheme));
            return null;
        }
        String[] stringNumber = text.split(",");
        ArrayList<ArrowNumber> list = new ArrayList<>();
        for (String num : stringNumber) {
            if (!num.isEmpty()) {
                ArrowNumber an = new ArrowNumber();
                an.number = Integer.parseInt(num);
                list.add(an);
            }
        }
        return list;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("arrow", Parcels.wrap(buildArrow()));
    }
}