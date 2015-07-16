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
import de.dreier.mytargets.views.DialogSpinner;
import de.dreier.mytargets.views.DistanceDialogSpinner;
import de.dreier.mytargets.views.DynamicItemLayout;

public class EditBowActivity extends EditWithImageActivity
        implements DynamicItemLayout.OnBindListener<EditBowActivity.SightSetting> {

    public static final String BOW_ID = "bow_id";
    private static final int REQ_SELECTED_DISTANCE = 1;
    public static final int RECURVE_BOW = 0;
    public static final int COMPOUND_BOW = 1;
    public static final int LONG_BOW = 2;
    public static final int BLANK_BOW = 3;
    public static final int HORSE_BOW = 4;
    public static final int YUMI = 5;
    private EditText name;
    private EditText brand;
    private EditText size;
    private EditText height;
    private EditText tiller;
    private EditText desc;
    private RadioButton recurveBow, compoundBow, longBow, blank, horse, yumi;
    private long mBowId = -1;
    private DynamicItemLayout<SightSetting> sight_settings;
    private SightSetting curSetting;
    private DistanceDialogSpinner curDistanceSpinner;

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
        sight_settings.setLayoutResource(R.layout.sight_settings_item, SightSetting.class);
        sight_settings.setOnBindListener(this);

        recurveBow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(RECURVE_BOW);
            }
        });
        compoundBow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(COMPOUND_BOW);
            }
        });
        longBow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(LONG_BOW);
            }
        });
        blank.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(BLANK_BOW);
            }
        });
        horse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(HORSE_BOW);
            }
        });
        yumi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(YUMI);
            }
        });

        ArrayList<SightSetting> sightSettingsList = null;
        if (savedInstanceState == null && mBowId != -1) {
            DatabaseManager db = DatabaseManager.getInstance(this);
            Bow bow = db.getBow(mBowId, false);
            setTitle(bow.name);
            name.setText(bow.name);
            brand.setText(bow.brand);
            size.setText(bow.size);
            height.setText(bow.height);
            tiller.setText(bow.tiller);
            desc.setText(bow.description);
            imageBitmap = bow.image;
            if (imageBitmap != null) {
                mImageView.setImageBitmap(imageBitmap);
            }
            mImageFile = bow.imageFile;
            setBowType(bow.type);
            sightSettingsList = db.getSettings(mBowId);
        } else if (savedInstanceState == null) {
            recurveBow.setChecked(true);
            sightSettingsList = new ArrayList<>();
            setTitle(R.string.new_bow);
        }

        if (savedInstanceState != null) {
            name.setText(savedInstanceState.getString("name"));
            brand.setText(savedInstanceState.getString("brand"));
            size.setText(savedInstanceState.getString("size"));
            height.setText(savedInstanceState.getString("height"));
            tiller.setText(savedInstanceState.getString("tiller"));
            desc.setText(savedInstanceState.getString("desc"));
            //noinspection unchecked
            sightSettingsList = (ArrayList<SightSetting>) savedInstanceState.getSerializable(
                    "settings");
        } else {
            if (sightSettingsList == null) {
                sight_settings.addItem(new SightSetting());
            } else {
                sight_settings.setList(sightSettingsList);
            }
        }
    }

    public static class SightSetting implements Serializable {
        public Distance distance = new Distance(18, Dimension.METER);
        public String value = "";

        public SightSetting() {
        }
    }

    @Override
    public void onBind(View view, final SightSetting sightSetting, int index) {
        final DistanceDialogSpinner distanceSpinner = (DistanceDialogSpinner) view
                .findViewById(R.id.distance_spinner);
        distanceSpinner.setOnResultListener(new DialogSpinner.OnResultListener() {
            @Override
            public void onResult(Intent data) {
                long id = data.getLongExtra("id", 0);
                distanceSpinner.setItemId(id);
                sightSetting.distance = Distance.fromId(id);
            }
        });
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
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sight_settings.remove(sightSetting, R.string.undo);
            }
        });
        distanceSpinner.setItemId(sightSetting.distance.getId());
        setting.setText(sightSetting.value);
    }

    @Override
    public void onSave() {
        DatabaseManager db = DatabaseManager.getInstance(this);

        Bow bow = new Bow();
        bow.setId(mBowId);
        bow.name = name.getText().toString();
        bow.brand = brand.getText().toString();
        bow.size = size.getText().toString();
        bow.height = height.getText().toString();
        bow.tiller = tiller.getText().toString();
        bow.description = desc.getText().toString();
        bow.type = getType();
        bow.imageFile = mImageFile;
        bow.image = imageBitmap;

        db.update(bow);
        db.updateSightSettings(bow.getId(), sight_settings.getList());
        finish();
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
        outState.putString("name", name.getText().toString());
        outState.putString("brand", brand.getText().toString());
        outState.putString("size", size.getText().toString());
        outState.putString("height", height.getText().toString());
        outState.putString("tiller", tiller.getText().toString());
        outState.putString("desc", desc.getText().toString());
        outState.putSerializable("settings", sight_settings.getList());
    }
}