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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import java.io.Serializable;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.DynamicItemLayout;

public class EditBowActivity extends EditWithImageActivity
        implements DynamicItemLayout.OnBindListener<EditBowActivity.SightSetting> {

    public static final String BOW_ID = "bow_id";
    private static final int RECURVE_BOW = 0;
    private static final int COMPOUND_BOW = 1;
    private static final int LONG_BOW = 2;
    private static final int BLANK_BOW = 3;
    private static final int HORSE_BOW = 4;
    private static final int YUMI = 5;
    private EditText name;
    private EditText brand;
    private EditText size;
    private EditText height;
    private EditText tiller;
    private EditText desc;
    private RadioButton recurveBow, compoundBow, longBow, blank, horse, yumi;
    private long mBowId = -1;
    private DynamicItemLayout<SightSetting> sight_settings;

    public EditBowActivity() {
        super(R.layout.activity_edit_bow, R.drawable.recurve_bow);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(BOW_ID)) {
            mBowId = intent.getLongExtra(BOW_ID, -1);
        }

        name = (EditText) findViewById(R.id.name);
        recurveBow = (RadioButton) findViewById(R.id.recurve);
        compoundBow = (RadioButton) findViewById(R.id.compound);
        longBow = (RadioButton) findViewById(R.id.longbow);
        blank = (RadioButton) findViewById(R.id.blank);
        horse = (RadioButton) findViewById(R.id.horse);
        yumi = (RadioButton) findViewById(R.id.yumi);
        brand = (EditText) findViewById(R.id.brand);
        size = (EditText) findViewById(R.id.size);
        height = (EditText) findViewById(R.id.brace_height);
        tiller = (EditText) findViewById(R.id.tiller);
        desc = (EditText) findViewById(R.id.desc);
        //noinspection unchecked
        sight_settings = (DynamicItemLayout<SightSetting>) findViewById(R.id.sight_settings);
        sight_settings.setLayoutResource(R.layout.dynamicitem_sight_settings, SightSetting.class);
        sight_settings.setOnBindListener(this);

        recurveBow.setOnClickListener(v -> setBowType(RECURVE_BOW));
        compoundBow.setOnClickListener(v -> setBowType(COMPOUND_BOW));
        longBow.setOnClickListener(v -> setBowType(LONG_BOW));
        blank.setOnClickListener(v -> setBowType(BLANK_BOW));
        horse.setOnClickListener(v -> setBowType(HORSE_BOW));
        yumi.setOnClickListener(v -> setBowType(YUMI));

        ArrayList<SightSetting> sightSettingsList = new ArrayList<>();
        if (savedInstanceState == null) {
            if (mBowId != -1) {
                // Load data from database
                DatabaseManager db = DatabaseManager.getInstance(this);
                Bow bow = db.getBow(mBowId);
                setBowValues(bow);
                sightSettingsList = db.getSightSettings(mBowId);
                setTitle(R.string.edit_bow);
            } else {
                // Set to default values
                recurveBow.setChecked(true);
                setTitle(R.string.new_bow);
                sightSettingsList.add(new SightSetting());
            }
        } else {
            // Restore values from before orientation change
            Bow bow = (Bow) savedInstanceState.getSerializable("bow");
            setBowValues(bow);
            //noinspection unchecked
            sightSettingsList = (ArrayList<SightSetting>) savedInstanceState //TODO integrate sight settings list in bow
                    .getSerializable("settings");

        }
        sight_settings.setList(sightSettingsList);
    }

    private void setBowValues(Bow bow) {
        setTitle(bow.name);
        name.setText(bow.name);
        brand.setText(bow.brand);
        size.setText(bow.size);
        height.setText(bow.height);
        tiller.setText(bow.tiller);
        desc.setText(bow.description);
        imageBitmap = bow.getImage(this);
        if (imageBitmap != null) {
            mImageView.setImageBitmap(imageBitmap);
        }
        imageFile = bow.imageFile;
        setBowType(bow.type);
    }

    public static class SightSetting implements Serializable {
        public Distance distance = new Distance(18, Dimension.METER);
        public String value = "";
    }

    @Override
    public void onBind(View view, final SightSetting sightSetting, int index) {
        final DistanceSelector distanceSpinner = (DistanceSelector) view
                .findViewById(R.id.distance_spinner);
        distanceSpinner.setOnUpdateListener(item -> sightSetting.distance = item);
        EditText setting = (EditText) view.findViewById(R.id.sight_setting);
        setting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sightSetting.value = s.toString();
            }
        });
        ImageButton remove = (ImageButton) view.findViewById(R.id.remove_sight_setting);
        remove.setOnClickListener(view1 -> sight_settings.remove(sightSetting, R.string.sight_setting_removed));
        distanceSpinner.setItem(sightSetting.distance);
        setting.setText(sightSetting.value);
    }

    @Override
    public void onSave() {
        DatabaseManager db = DatabaseManager.getInstance(this);
        Bow bow = buildBow();
        db.update(bow);
        db.updateSightSettings(bow.getId(), sight_settings.getList());
        finish();
    }

    private Bow buildBow() {
        Bow bow = new Bow();
        bow.setId(mBowId);
        bow.name = name.getText().toString();
        bow.brand = brand.getText().toString();
        bow.size = size.getText().toString();
        bow.height = height.getText().toString();
        bow.tiller = tiller.getText().toString();
        bow.description = desc.getText().toString();
        bow.type = getType();
        bow.setImage(imageFile, imageBitmap);
        Log.d("buildBow", imageFile);
        return bow;
    }

    private void setBowType(int type) {
        recurveBow.setChecked(type == RECURVE_BOW);
        compoundBow.setChecked(type == COMPOUND_BOW);
        longBow.setChecked(type == LONG_BOW);
        blank.setChecked(type == BLANK_BOW);
        horse.setChecked(type == HORSE_BOW);
        yumi.setChecked(type == YUMI);
    }

    private int getType() {
        if (recurveBow.isChecked()) {
            return RECURVE_BOW;
        } else if (compoundBow.isChecked()) {
            return COMPOUND_BOW;
        } else if (longBow.isChecked()) {
            return LONG_BOW;
        } else if (blank.isChecked()) {
            return BLANK_BOW;
        } else if (horse.isChecked()) {
            return HORSE_BOW;
        } else if (yumi.isChecked()) {
            return YUMI;
        } else {
            return RECURVE_BOW;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("bow", buildBow());
        outState.putSerializable("settings", sight_settings.getList());
    }
}