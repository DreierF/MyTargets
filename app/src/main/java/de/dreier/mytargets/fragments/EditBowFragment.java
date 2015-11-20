/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import java.io.File;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.BowDataSource;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.SightSetting;
import de.dreier.mytargets.views.DynamicItemLayout;
import de.dreier.mytargets.views.selector.DistanceSelector;

public class EditBowFragment extends EditWithImageFragmentBase
        implements DynamicItemLayout.OnBindListener<SightSetting> {

    public static final String BOW_ID = "bow_id";
    private static final int RECURVE_BOW = 0;
    private static final int COMPOUND_BOW = 1;
    private static final int LONG_BOW = 2;
    private static final int BARE_BOW = 3;
    private static final int HORSE_BOW = 4;
    private static final int YUMI = 5;
    private EditText brand;
    private EditText size;
    private EditText height;
    private EditText tiller;
    private EditText limbs;
    private EditText sight;
    private EditText drawWeight;
    private EditText desc;
    private RadioButton recurveBow, compoundBow, longBow, blank, horse, yumi;
    private long mBowId = -1;
    private DynamicItemLayout<SightSetting> sight_settings;

    public EditBowFragment() {
        super(R.layout.fragment_edit_bow, R.drawable.recurve_bow);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);


        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(BOW_ID)) {
            mBowId = bundle.getLong(BOW_ID, -1);
        }

        recurveBow = (RadioButton) rootView.findViewById(R.id.recurve);
        compoundBow = (RadioButton) rootView.findViewById(R.id.compound);
        longBow = (RadioButton) rootView.findViewById(R.id.longbow);
        blank = (RadioButton) rootView.findViewById(R.id.blank);
        horse = (RadioButton) rootView.findViewById(R.id.horse);
        yumi = (RadioButton) rootView.findViewById(R.id.yumi);
        brand = (EditText) rootView.findViewById(R.id.brand);
        size = (EditText) rootView.findViewById(R.id.size);
        height = (EditText) rootView.findViewById(R.id.brace_height);
        tiller = (EditText) rootView.findViewById(R.id.tiller);
        limbs = (EditText) rootView.findViewById(R.id.limbs);
        sight = (EditText) rootView.findViewById(R.id.sight);
        drawWeight = (EditText) rootView.findViewById(R.id.draw_weight);
        desc = (EditText) rootView.findViewById(R.id.desc);
        //noinspection unchecked
        sight_settings = (DynamicItemLayout<SightSetting>) rootView.findViewById(R.id.sight_settings);
        sight_settings.setLayoutResource(R.layout.dynamicitem_sight_settings, SightSetting.class);
        sight_settings.setOnBindListener(this);

        recurveBow.setOnClickListener(v -> setBowType(RECURVE_BOW));
        compoundBow.setOnClickListener(v -> setBowType(COMPOUND_BOW));
        longBow.setOnClickListener(v -> setBowType(LONG_BOW));
        blank.setOnClickListener(v -> setBowType(BARE_BOW));
        horse.setOnClickListener(v -> setBowType(HORSE_BOW));
        yumi.setOnClickListener(v -> setBowType(YUMI));

        ArrayList<SightSetting> sightSettingsList = new ArrayList<>();
        if (savedInstanceState == null) {
            if (mBowId != -1) {
                // Load data from database
                Bow bow = new BowDataSource(getContext()).get(mBowId);
                setBowValues(bow);
            } else {
                // Set to default values
                recurveBow.setChecked(true);
                setTitle(R.string.my_bow);
                sightSettingsList.add(new SightSetting());
                sight_settings.setList(sightSettingsList);
            }
        } else {
            // Restore values from before orientation change
            Bow bow = (Bow) savedInstanceState.getSerializable("bow");
            setBowValues(bow);

        }
        return rootView;
    }

    private void setBowValues(Bow bow) {
        setTitle(bow.name);
        brand.setText(bow.brand);
        size.setText(bow.size);
        height.setText(bow.height);
        tiller.setText(bow.tiller);
        limbs.setText(bow.limbs);
        sight.setText(bow.sight);
        drawWeight.setText(bow.drawWeight);
        desc.setText(bow.description);
        imageBitmap = bow.getImage(getContext());
        if (imageBitmap != null) {
            mImageView.setImageBitmap(imageBitmap);
        }
        imageFile = bow.imageFile;
        setBowType(bow.type);
        sight_settings.setList(bow.sightSettings);
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
        new BowDataSource(getContext())
                .update(buildBow());
        getActivity().finish();
    }

    private Bow buildBow() {
        Bow bow = new Bow();
        bow.setId(mBowId);
        bow.name = getName();
        bow.brand = brand.getText().toString();
        bow.size = size.getText().toString();
        bow.height = height.getText().toString();
        bow.tiller = tiller.getText().toString();
        bow.limbs = limbs.getText().toString();
        bow.sight = sight.getText().toString();
        bow.drawWeight = drawWeight.getText().toString();
        bow.description = desc.getText().toString();
        bow.type = getType();

        // Delete old file
        if (oldImageFile != null) {
            File f = new File(getContext().getFilesDir(), oldImageFile);
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }
        bow.setImage(imageFile, imageBitmap);
        bow.sightSettings = sight_settings.getList();
        return bow;
    }

    private void setBowType(int type) {
        recurveBow.setChecked(type == RECURVE_BOW);
        compoundBow.setChecked(type == COMPOUND_BOW);
        longBow.setChecked(type == LONG_BOW);
        blank.setChecked(type == BARE_BOW);
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
            return BARE_BOW;
        } else if (horse.isChecked()) {
            return HORSE_BOW;
        } else if (yumi.isChecked()) {
            return YUMI;
        } else {
            return RECURVE_BOW;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("bow", buildBow());
    }
}