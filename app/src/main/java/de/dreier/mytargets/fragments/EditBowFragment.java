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

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
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

    private long mBowId = -1;

    @Bind(R.id.recurve)
    RadioButton recurveBow;
    @Bind(R.id.compound)
    RadioButton compoundBow;
    @Bind(R.id.longbow)
    RadioButton longBow;
    @Bind(R.id.blank)
    RadioButton blankBow;
    @Bind(R.id.horse)
    RadioButton horseBow;
    @Bind(R.id.yumi)
    RadioButton yumiBow;

    @Bind(R.id.brand)
    EditText brand;
    @Bind(R.id.size)
    EditText size;
    @Bind(R.id.braceHeight)
    EditText braceHeight;
    @Bind(R.id.tiller)
    EditText tiller;
    @Bind(R.id.limbs)
    EditText limbs;
    @Bind(R.id.sight)
    EditText sight;
    @Bind(R.id.drawWeight)
    EditText drawWeight;
    @Bind(R.id.stabilizer)
    EditText stabilizer;
    @Bind(R.id.clicker)
    EditText clicker;
    @Bind(R.id.desc)
    EditText desc;

    @Bind(R.id.sightSettings)
    DynamicItemLayout<SightSetting> sightSettings;

    public EditBowFragment() {
        super(R.layout.fragment_edit_bow, R.drawable.recurve_bow);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(BOW_ID)) {
            mBowId = bundle.getLong(BOW_ID, -1);
        }

        sightSettings.setLayoutResource(R.layout.dynamicitem_sight_settings, SightSetting.class);
        sightSettings.setOnBindListener(this);

        recurveBow.setOnClickListener(v -> setBowType(RECURVE_BOW));
        compoundBow.setOnClickListener(v -> setBowType(COMPOUND_BOW));
        longBow.setOnClickListener(v -> setBowType(LONG_BOW));
        blankBow.setOnClickListener(v -> setBowType(BARE_BOW));
        horseBow.setOnClickListener(v -> setBowType(HORSE_BOW));
        yumiBow.setOnClickListener(v -> setBowType(YUMI));

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
                sightSettings.setList(sightSettingsList);
            }
        } else {
            // Restore values from before orientation change
            Bow bow = Parcels.unwrap(savedInstanceState.getParcelable("bow"));
            setBowValues(bow);

        }
        return rootView;
    }

    private void setBowValues(Bow bow) {
        setTitle(bow.name);
        brand.setText(bow.brand);
        size.setText(bow.size);
        braceHeight.setText(bow.height);
        tiller.setText(bow.tiller);
        limbs.setText(bow.limbs);
        sight.setText(bow.sight);
        drawWeight.setText(bow.drawWeight);
        stabilizer.setText(bow.stabilizer);
        clicker.setText(bow.clicker);
        desc.setText(bow.description);
        imageBitmap = bow.getImage(getContext());
        if (imageBitmap != null) {
            imageView.setImageBitmap(imageBitmap);
        }
        imageFile = bow.imageFile;
        setBowType(bow.type);
        sightSettings.setList(bow.sightSettings);
    }

    @Override
    public void onBind(View view, final SightSetting sightSetting, int index) {
        final DistanceSelector distanceSpinner = (DistanceSelector) view
                .findViewById(R.id.distanceSpinner);
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
        remove.setOnClickListener(view1 -> sightSettings.remove(sightSetting, R.string.sight_setting_removed));
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
        bow.height = braceHeight.getText().toString();
        bow.tiller = tiller.getText().toString();
        bow.limbs = limbs.getText().toString();
        bow.sight = sight.getText().toString();
        bow.drawWeight = drawWeight.getText().toString();
        bow.stabilizer = stabilizer.getText().toString();
        bow.clicker = clicker.getText().toString();
        bow.description = desc.getText().toString();
        bow.type = getType();

        // Delete old file
        if (oldImageFile != null) {
            File f = new File(getContext().getFilesDir(), oldImageFile);
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }
        bow.setImage(imageFile, imageBitmap);
        bow.sightSettings = sightSettings.getList();
        return bow;
    }

    private void setBowType(int type) {
        recurveBow.setChecked(type == RECURVE_BOW);
        compoundBow.setChecked(type == COMPOUND_BOW);
        longBow.setChecked(type == LONG_BOW);
        blankBow.setChecked(type == BARE_BOW);
        horseBow.setChecked(type == HORSE_BOW);
        yumiBow.setChecked(type == YUMI);
    }

    private int getType() {
        if (recurveBow.isChecked()) {
            return RECURVE_BOW;
        } else if (compoundBow.isChecked()) {
            return COMPOUND_BOW;
        } else if (longBow.isChecked()) {
            return LONG_BOW;
        } else if (blankBow.isChecked()) {
            return BARE_BOW;
        } else if (horseBow.isChecked()) {
            return HORSE_BOW;
        } else if (yumiBow.isChecked()) {
            return YUMI;
        } else {
            return RECURVE_BOW;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("bow", Parcels.wrap(buildBow()));
    }
}