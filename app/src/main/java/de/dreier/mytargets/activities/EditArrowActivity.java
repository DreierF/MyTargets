/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Arrow;

public class EditArrowActivity extends EditWithImageActivity {

    public static final String ARROW_ID = "arrow_id";

    private EditText length, material, spine, weight, vanes, nock, comment;
    private long mArrowId = -1;
    private EditText arrowNumbers;

    public EditArrowActivity() {
        super(R.layout.activity_edit_arrow, R.drawable.arrows);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ARROW_ID)) {
            mArrowId = intent.getLongExtra(ARROW_ID, -1);
        }

        length = (EditText) findViewById(R.id.arrow_length);
        material = (EditText) findViewById(R.id.arrow_material);
        spine = (EditText) findViewById(R.id.arrow_spine);
        weight = (EditText) findViewById(R.id.arrow_weight);
        vanes = (EditText) findViewById(R.id.arrow_vanes);
        nock = (EditText) findViewById(R.id.arrow_nock);
        comment = (EditText) findViewById(R.id.arrow_comment);
        arrowNumbers = (EditText) findViewById(R.id.arrow_numbers);

        setTitle(R.string.my_arrow);
        ArrayList<Integer> arrowNumbersList = new ArrayList<>();
        if (savedInstanceState == null) {
            if (mArrowId != -1) {
                // Load data from database
                DatabaseManager db = DatabaseManager.getInstance(this);
                Arrow arrow = db.getArrow(mArrowId);
                setTitle(arrow.name);
                length.setText(arrow.length);
                material.setText(arrow.material);
                spine.setText(arrow.spine);
                weight.setText(arrow.weight);
                vanes.setText(arrow.vanes);
                nock.setText(arrow.nock);
                comment.setText(arrow.comment);
                imageBitmap = arrow.getImage(this);
                if (imageBitmap != null) {
                    mImageView.setImageBitmap(imageBitmap);
                }
                imageFile = arrow.imageFile;
                arrowNumbersList = db.getArrowNumbers(mArrowId);
            }
            String text = "";
            for (Integer number : arrowNumbersList) {
                if (!text.isEmpty()) {
                    text += ",";
                }
                text += number;
            }
            arrowNumbers.setText(text);
        } else {
            // Restore values from before orientation change
            length.setText(savedInstanceState.getString("length"));
            material.setText(savedInstanceState.getString("material"));
            spine.setText(savedInstanceState.getString("spine"));
            weight.setText(savedInstanceState.getString("weight"));
            vanes.setText(savedInstanceState.getString("vanes"));
            nock.setText(savedInstanceState.getString("nock"));
            comment.setText(savedInstanceState.getString("comment"));
            arrowNumbers.setText(savedInstanceState.getString("arrows"));
        }
    }

    @Override
    public void onSave() {
        ArrayList<Integer> numbers = getArrowNumbers();
        if (numbers == null) {
            return;
        }
        DatabaseManager db = DatabaseManager.getInstance(this);

        Arrow arrow = new Arrow();
        arrow.setId(mArrowId);
        arrow.name = getName();
        arrow.length = length.getText().toString();
        arrow.material = material.getText().toString();
        arrow.spine = spine.getText().toString();
        arrow.weight = weight.getText().toString();
        arrow.vanes = vanes.getText().toString();
        arrow.nock = nock.getText().toString();
        arrow.comment = comment.getText().toString();
        arrow.setImage(imageFile, imageBitmap);
        arrow.numbers = numbers;

        db.update(arrow);
        finish();
    }

    private ArrayList<Integer> getArrowNumbers() {
        String text = arrowNumbers.getText().toString().replace(" ", "");
        if (!text.matches("([0-9]*(,[0-9]*)*)?")) {
            arrowNumbers.setError(getString(R.string.not_matches_sheme));
            return null;
        }
        String[] stringNumber = text.split(",");
        ArrayList<Integer> list = new ArrayList<>();
        for (String num : stringNumber) {
            if (!num.isEmpty()) {
                list.add(Integer.parseInt(num));
            }
        }
        return list;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("length", length.getText().toString());
        outState.putString("material", material.getText().toString());
        outState.putString("spine", spine.getText().toString());
        outState.putString("weight", weight.getText().toString());
        outState.putString("vanes", vanes.getText().toString());
        outState.putString("nock", nock.getText().toString());
        outState.putString("comment", comment.getText().toString());
        outState.putString("arrows", arrowNumbers.getText().toString());
    }
}