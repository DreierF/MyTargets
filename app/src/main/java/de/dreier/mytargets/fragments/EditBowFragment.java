/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.adapters.DynamicItemHolder;
import de.dreier.mytargets.databinding.EditBowFragmentBinding;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.databinding.ItemSightMarkBinding;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.db.SightSetting;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.views.selector.SelectorBase;
import de.dreier.mytargets.views.selector.SimpleDistanceSelector;
import icepick.State;

import static de.dreier.mytargets.shared.models.EBowType.BARE_BOW;
import static de.dreier.mytargets.shared.models.EBowType.COMPOUND_BOW;
import static de.dreier.mytargets.shared.models.EBowType.HORSE_BOW;
import static de.dreier.mytargets.shared.models.EBowType.LONG_BOW;
import static de.dreier.mytargets.shared.models.EBowType.RECURVE_BOW;
import static de.dreier.mytargets.shared.models.EBowType.YUMI;

public class EditBowFragment extends EditWithImageFragmentBase {

    private static final String BOW_ID = "bow_id";
    @State(ParcelsBundler.class)
    Bow bow;
    private EditBowFragmentBinding contentBinding;
    private SightSettingsAdapter adapter;

    public EditBowFragment() {
        super(R.drawable.recurve_bow);
    }

    @NonNull
    public static IntentWrapper createIntent() {
        return new IntentWrapper(SimpleFragmentActivityBase.EditBowActivity.class);
    }

    @NonNull
    public static IntentWrapper editIntent(Bow bow) {
        return new IntentWrapper(SimpleFragmentActivityBase.EditBowActivity.class)
                .with(BOW_ID, bow.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        contentBinding = EditBowFragmentBinding.inflate(inflater, binding.content, true);
        contentBinding.addButton.setOnClickListener((view) -> onAddSightSetting());

        // TODO make this a selector
        contentBinding.recurveBow.setOnClickListener(v -> setBowType(RECURVE_BOW));
        contentBinding.compoundBow.setOnClickListener(v -> setBowType(COMPOUND_BOW));
        contentBinding.longBow.setOnClickListener(v -> setBowType(LONG_BOW));
        contentBinding.blankBow.setOnClickListener(v -> setBowType(BARE_BOW));
        contentBinding.horseBow.setOnClickListener(v -> setBowType(HORSE_BOW));
        contentBinding.yumiBow.setOnClickListener(v -> setBowType(YUMI));

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey(BOW_ID)) {
                // Load data from database
                long bowId = bundle.getLong(BOW_ID);
                bow = Bow.get(bowId);
                setImageFile(bow.imageFile);
            } else {
                // Set to default values
                bow = new Bow();
                bow.name = getString(R.string.my_bow);
                bow.type = RECURVE_BOW;
                bow.getSightSettings().add(new SightSetting());
                setImageFile(null);
            }

            ToolbarUtils.setTitle(this, bow.name);
            contentBinding.setBow(bow);
            setBowType(bow.type);
        } else {
            contentBinding.setBow(bow);
        }

        loadImage(imageFile);
        adapter = new SightSettingsAdapter(this, bow.getSightSettings());
        contentBinding.sightSettings.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        bow = buildBow();
        super.onSaveInstanceState(outState);
    }

    private void onAddSightSetting() {
        bow.getSightSettings().add(new SightSetting());
        adapter.notifyItemInserted(bow.getSightSettings().size() - 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == SimpleDistanceSelector.SIMPLE_DISTANCE_REQUEST_CODE) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            final int index = intentData.getInt(SelectorBase.INDEX);
            final Parcelable parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
            bow.getSightSettings().get(index).distance = Parcels.unwrap(parcelable);
            adapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onSave() {
        super.onSave();
        buildBow().save();
        finish();
    }

    private Bow buildBow() {
        bow.name = contentBinding.name.getText().toString();
        bow.brand = contentBinding.brand.getText().toString();
        bow.size = contentBinding.size.getText().toString();
        bow.braceHeight = contentBinding.braceHeight.getText().toString();
        bow.tiller = contentBinding.tiller.getText().toString();
        bow.limbs = contentBinding.limbs.getText().toString();
        bow.sight = contentBinding.sight.getText().toString();
        bow.drawWeight = contentBinding.drawWeight.getText().toString();
        bow.stabilizer = contentBinding.stabilizer.getText().toString();
        bow.clicker = contentBinding.clicker.getText().toString();
        bow.description = contentBinding.description.getText().toString();
        bow.type = getType();
        bow.imageFile = getImageFile();
        bow.thumbnail = getThumbnail();
        return bow;
    }

    private void setBowType(EBowType type) {
        contentBinding.recurveBow.setChecked(type == RECURVE_BOW);
        contentBinding.compoundBow.setChecked(type == COMPOUND_BOW);
        contentBinding.longBow.setChecked(type == LONG_BOW);
        contentBinding.blankBow.setChecked(type == BARE_BOW);
        contentBinding.horseBow.setChecked(type == HORSE_BOW);
        contentBinding.yumiBow.setChecked(type == YUMI);
    }

    private EBowType getType() {
        if (contentBinding.recurveBow.isChecked()) {
            return RECURVE_BOW;
        } else if (contentBinding.compoundBow.isChecked()) {
            return COMPOUND_BOW;
        } else if (contentBinding.longBow.isChecked()) {
            return LONG_BOW;
        } else if (contentBinding.blankBow.isChecked()) {
            return BARE_BOW;
        } else if (contentBinding.horseBow.isChecked()) {
            return HORSE_BOW;
        } else if (contentBinding.yumiBow.isChecked()) {
            return YUMI;
        } else {
            return RECURVE_BOW;
        }
    }

    private static class SightSettingHolder extends DynamicItemHolder<SightSetting> {

        private final ItemSightMarkBinding binding;

        SightSettingHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
            binding.sightSetting.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    item.value = s.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        @Override
        public void onBind(SightSetting sightSetting, int position, Fragment fragment, View.OnClickListener removeListener) {
            item = sightSetting;
            binding.distance.setOnActivityResultContext(fragment);
            binding.distance.setItemIndex(position);
            binding.distance.setItem(sightSetting.distance);
            binding.sightSetting.setText(sightSetting.value);
            binding.removeSightSetting.setOnClickListener(removeListener);
        }
    }

    private class SightSettingsAdapter extends DynamicItemAdapter<SightSetting> {
        SightSettingsAdapter(Fragment fragment, List<SightSetting> list) {
            super(fragment, list, R.string.sight_setting_removed);
        }

        @Override
        public DynamicItemHolder<SightSetting> onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.item_sight_mark, parent, false);
            return new SightSettingHolder(v);
        }
    }
}
