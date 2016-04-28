/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.ArrowNumber;

public class EditArrowFragment extends EditWithImageFragmentBase {

    public static final String ARROW_ID = "arrow_id";
    private long mArrowId = -1;

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
                    imageView.setImageBitmap(imageBitmap);
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

    private Arrow buildArrow() {
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
}