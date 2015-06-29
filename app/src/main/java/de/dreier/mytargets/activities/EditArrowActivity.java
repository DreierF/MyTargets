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

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Arrow;

public class EditArrowActivity extends EditWithImageActivity {

    public static final String ARROW_ID = "arrow_id";

    private EditText name, length, material, spine, weight, vanes, nock, comment;
    private long mArrowId = -1;

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

        name = (EditText) findViewById(R.id.arrow_name);
        length = (EditText) findViewById(R.id.arrow_length);
        material = (EditText) findViewById(R.id.arrow_material);
        spine = (EditText) findViewById(R.id.arrow_spine);
        weight = (EditText) findViewById(R.id.arrow_weight);
        vanes = (EditText) findViewById(R.id.arrow_vanes);
        nock = (EditText) findViewById(R.id.arrow_nock);
        comment = (EditText) findViewById(R.id.arrow_comment);

        setTitle(R.string.new_arrow);
        if (savedInstanceState == null && mArrowId != -1) {
            Arrow arrow = DatabaseManager.getInstance(this).getArrow(mArrowId, false);
            setTitle(arrow.name);
            name.setText(arrow.name);
            length.setText(arrow.length);
            material.setText(arrow.material);
            spine.setText(arrow.spine);
            weight.setText(arrow.weight);
            vanes.setText(arrow.vanes);
            nock.setText(arrow.nock);
            comment.setText(arrow.comment);
            imageBitmap = arrow.image;
            if (imageBitmap != null) {
                mImageView.setImageBitmap(imageBitmap);
            }
            mImageFile = arrow.imageFile;
        } else if (savedInstanceState != null) {
            name.setText(savedInstanceState.getString("name"));
            length.setText(savedInstanceState.getString("length"));
            material.setText(savedInstanceState.getString("material"));
            spine.setText(savedInstanceState.getString("spine"));
            weight.setText(savedInstanceState.getString("weight"));
            vanes.setText(savedInstanceState.getString("vanes"));
            nock.setText(savedInstanceState.getString("nock"));
            comment.setText(savedInstanceState.getString("comment"));
        }
    }

    @Override
    public void onSave() {
        DatabaseManager db = DatabaseManager.getInstance(this);

        Arrow arrow = new Arrow();
        arrow.setId(mArrowId);
        arrow.name = name.getText().toString();
        arrow.length = length.getText().toString();
        arrow.material = material.getText().toString();
        arrow.spine = spine.getText().toString();
        arrow.weight = weight.getText().toString();
        arrow.vanes = vanes.getText().toString();
        arrow.nock = nock.getText().toString();
        arrow.comment = comment.getText().toString();
        arrow.imageFile = mImageFile;
        arrow.image = imageBitmap;

        db.update(arrow);
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", name.getText().toString());
        outState.putString("length", length.getText().toString());
        outState.putString("material", material.getText().toString());
        outState.putString("spine", spine.getText().toString());
        outState.putString("weight", weight.getText().toString());
        outState.putString("vanes", vanes.getText().toString());
        outState.putString("nock", nock.getText().toString());
        outState.putString("comment", comment.getText().toString());
    }
}