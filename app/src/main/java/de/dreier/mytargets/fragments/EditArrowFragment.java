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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.ArrowNumber;

public class EditArrowFragment extends EditWithImageFragmentBase {

    public static final String ARROW_ID = "arrow_id";

    private EditText length, material, spine, weight, tipWeight, vanes, nock, comment;
    private long mArrowId = -1;
    private EditText arrowNumbers;

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

        length = (EditText) rootView.findViewById(R.id.arrow_length);
        material = (EditText) rootView.findViewById(R.id.arrow_material);
        spine = (EditText) rootView.findViewById(R.id.arrow_spine);
        weight = (EditText) rootView.findViewById(R.id.arrow_weight);
        tipWeight = (EditText) rootView.findViewById(R.id.arrow_tip_weight);
        vanes = (EditText) rootView.findViewById(R.id.arrow_vanes);
        nock = (EditText) rootView.findViewById(R.id.arrow_nock);
        comment = (EditText) rootView.findViewById(R.id.arrow_comment);
        arrowNumbers = (EditText) rootView.findViewById(R.id.arrow_numbers);

        setTitle(R.string.my_arrow);
        List<ArrowNumber> arrowNumbersList = new ArrayList<>();
        if (savedInstanceState == null) {
            if (mArrowId != -1) {
                // Load data from database
                ArrowDataSource db = new ArrowDataSource(getContext());
                Arrow arrow = db.get(mArrowId);
                setTitle(arrow.name);
                length.setText(arrow.length);
                material.setText(arrow.material);
                spine.setText(arrow.spine);
                weight.setText(arrow.weight);
                tipWeight.setText(arrow.tipWeight);
                vanes.setText(arrow.vanes);
                nock.setText(arrow.nock);
                comment.setText(arrow.comment);
                imageBitmap = arrow.getImage(getContext());
                if (imageBitmap != null) {
                    mImageView.setImageBitmap(imageBitmap);
                }
                imageFile = arrow.imageFile;
                arrowNumbersList = arrow.numbers;
            }
            String text = "";
            for (ArrowNumber arrowNumber : arrowNumbersList) {
                if (!text.isEmpty()) {
                    text += ",";
                }
                text += arrowNumber.number;
            }
            arrowNumbers.setText(text);
        } else {
            // Restore values from before orientation change
            length.setText(savedInstanceState.getString("length"));
            material.setText(savedInstanceState.getString("material"));
            spine.setText(savedInstanceState.getString("spine"));
            weight.setText(savedInstanceState.getString("weight"));
            tipWeight.setText(savedInstanceState.getString("tipWeight"));
            vanes.setText(savedInstanceState.getString("vanes"));
            nock.setText(savedInstanceState.getString("nock"));
            comment.setText(savedInstanceState.getString("comment"));
            arrowNumbers.setText(savedInstanceState.getString("arrows"));
        }
        return rootView;
    }

    @Override
    public void onSave() {
        Arrow arrow = buildArrow();
        if (arrow == null) {
            return;
        }
        new ArrowDataSource(getContext()).update(arrow);
        getActivity().finish();
    }

    protected Arrow buildArrow() {
        ArrayList<ArrowNumber> numbers = getArrowNumbers();
        if (numbers == null) {
            return null;
        }
        Arrow arrow = new Arrow();
        arrow.setId(mArrowId);
        arrow.name = getName();
        arrow.length = length.getText().toString();
        arrow.material = material.getText().toString();
        arrow.spine = spine.getText().toString();
        arrow.weight = weight.getText().toString();
        arrow.tipWeight = tipWeight.getText().toString();
        arrow.vanes = vanes.getText().toString();
        arrow.nock = nock.getText().toString();
        arrow.comment = comment.getText().toString();

        // Delete old file
        if (oldImageFile != null) {
            File f = new File(getContext().getFilesDir(), oldImageFile);
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }
        arrow.setImage(imageFile, imageBitmap);
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
        outState.putString("length", length.getText().toString());
        outState.putString("material", material.getText().toString());
        outState.putString("spine", spine.getText().toString());
        outState.putString("weight", weight.getText().toString());
        outState.putString("tipWeight", tipWeight.getText().toString());
        outState.putString("vanes", vanes.getText().toString());
        outState.putString("nock", nock.getText().toString());
        outState.putString("comment", comment.getText().toString());
        outState.putString("arrows", arrowNumbers.getText().toString());
    }
}