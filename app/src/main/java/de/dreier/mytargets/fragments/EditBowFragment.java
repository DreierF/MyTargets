/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.adapters.DynamicItemHolder;
import de.dreier.mytargets.managers.dao.BowDataSource;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.SightSetting;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.views.selector.SelectorBase;
import de.dreier.mytargets.views.selector.SimpleDistanceSelector;
import icepick.Icepick;
import icepick.State;

import static de.dreier.mytargets.shared.models.EBowType.BARE_BOW;
import static de.dreier.mytargets.shared.models.EBowType.COMPOUND_BOW;
import static de.dreier.mytargets.shared.models.EBowType.HORSE_BOW;
import static de.dreier.mytargets.shared.models.EBowType.LONG_BOW;
import static de.dreier.mytargets.shared.models.EBowType.RECURVE_BOW;
import static de.dreier.mytargets.shared.models.EBowType.YUMI;

public class EditBowFragment extends EditWithImageFragmentBase {

    public static final String BOW_ID = "bow_id";

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

    @Bind(R.id.name)
    EditText name;
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
    RecyclerView sightSettings;

    private long bowId = -1;

    @State(ParcelsBundler.class)
    ArrayList<SightSetting> sightSettingsList = new ArrayList<>();

    private SightSettingsAdapter adapter;

    public EditBowFragment() {
        super(R.layout.fragment_edit_bow, R.drawable.recurve_bow);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        Icepick.restoreInstanceState(this, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(BOW_ID)) {
            bowId = bundle.getLong(BOW_ID, -1);
        }

        // TODO make this a selector
        recurveBow.setOnClickListener(v -> setBowType(RECURVE_BOW));
        compoundBow.setOnClickListener(v -> setBowType(COMPOUND_BOW));
        longBow.setOnClickListener(v -> setBowType(LONG_BOW));
        blankBow.setOnClickListener(v -> setBowType(BARE_BOW));
        horseBow.setOnClickListener(v -> setBowType(HORSE_BOW));
        yumiBow.setOnClickListener(v -> setBowType(YUMI));

        if (savedInstanceState == null) {
            if (bowId != -1) {
                // Load data from database
                Bow bow = new BowDataSource().get(bowId);
                setBowValues(bow);
            } else {
                // Set to default values
                imageFile = null;
                setTitle(R.string.my_bow);
                name.setText(R.string.my_bow);
                recurveBow.setChecked(true);
                sightSettingsList.add(new SightSetting());
            }
        }

        loadImage(imageFile);
        adapter = new SightSettingsAdapter(this, sightSettingsList);
        sightSettings.setAdapter(adapter);
        return rootView;
    }

    @OnClick(R.id.addButton)
    public void onAddSightSetting() {
        sightSettingsList.add(new SightSetting());
        adapter.notifyItemInserted(sightSettingsList.size() - 1);
    }

    private void setBowValues(Bow bow) {
        setTitle(bow.name);
        name.setText(bow.name);
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
        setImageFile(bow.imageFile);
        setBowType(bow.type);
        sightSettingsList = bow.sightSettings;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == SimpleDistanceSelector.SIMPLE_DISTANCE_REQUEST_CODE) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            final int index = intentData.getInt(SelectorBase.INDEX);
            final Parcelable parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
            sightSettingsList.get(index).distance = Parcels.unwrap(parcelable);
            adapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onSave() {
        super.onSave();
        new BowDataSource()
                .update(buildBow());
        getActivity().finish();
    }

    private Bow buildBow() {
        Bow bow = new Bow();
        bow.setId(bowId);
        bow.name = name.getText().toString();
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
        bow.imageFile = getImageFile();
        bow.thumb = getThumbnail();
        bow.sightSettings = sightSettingsList;
        return bow;
    }

    private void setBowType(EBowType type) {
        recurveBow.setChecked(type == RECURVE_BOW);
        compoundBow.setChecked(type == COMPOUND_BOW);
        longBow.setChecked(type == LONG_BOW);
        blankBow.setChecked(type == BARE_BOW);
        horseBow.setChecked(type == HORSE_BOW);
        yumiBow.setChecked(type == YUMI);
    }

    private EBowType getType() {
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

    public static class SightSettingHolder extends DynamicItemHolder<SightSetting> {
        @Bind(R.id.distanceSpinner)
        SimpleDistanceSelector distanceSpinner;
        @Bind(R.id.sightSetting)
        EditText setting;
        @Bind(R.id.removeSightSetting)
        ImageButton remove;

        public SightSettingHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnTextChanged(R.id.sightSetting)
        public void onTextChanged(CharSequence s) {
            item.value = s.toString();
        }

        @Override
        public void onBind(SightSetting sightSetting, int position, Fragment fragment, View.OnClickListener removeListener) {
            item = sightSetting;
            distanceSpinner.setOnActivityResultContext(fragment);
            distanceSpinner.setItemIndex(position);
            distanceSpinner.setItem(sightSetting.distance);
            setting.setText(sightSetting.value);
            remove.setOnClickListener(removeListener);
        }
    }

    private class SightSettingsAdapter extends DynamicItemAdapter<SightSetting> {
        public SightSettingsAdapter(Fragment fragment, List<SightSetting> list) {
            super(fragment, list, R.string.sight_setting_removed);
        }

        @Override
        public DynamicItemHolder<SightSetting> onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.dynamicitem_sight_settings, parent, false);
            return new SightSettingHolder(v);
        }
    }
}