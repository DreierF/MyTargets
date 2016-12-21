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
import de.dreier.mytargets.databinding.ItemSightMarkBinding;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.SightMark;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.views.selector.SelectorBase;
import de.dreier.mytargets.views.selector.SimpleDistanceSelector;
import icepick.State;

public class EditBowFragment extends EditWithImageFragmentBase {

    public static final String BOW_TYPE = "bow_type";
    private static final String BOW_ID = "bow_id";
    @State(ParcelsBundler.class)
    Bow bow;
    private EditBowFragmentBinding contentBinding;
    private SightSettingsAdapter adapter;

    public EditBowFragment() {
        super(R.drawable.recurve_bow);
    }

    @NonNull
    public static IntentWrapper createIntent(EBowType bowType) {
        return new IntentWrapper(SimpleFragmentActivityBase.EditBowActivity.class)
                .with(EditBowFragment.BOW_TYPE, bowType.name());
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

        EBowType bowType = EBowType
                .valueOf(getArguments().getString(BOW_TYPE, EBowType.RECURVE_BOW.name()));

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey(BOW_ID)) {
                // Load data from database
                bow = Bow.get(bundle.getLong(BOW_ID));
                setImageFile(bow.images.size() > 0 ? bow.images.get(0) : null);
            } else {
                // Set to default values
                bow = new Bow();
                bow.name = getString(R.string.my_bow);
                bow.type = bowType;
                bow.getSightMarks().add(new SightMark());
                setImageFile(null);
            }

            ToolbarUtils.setTitle(this, bow.name);
            contentBinding.setBow(bow);
        } else {
            contentBinding.setBow(bow);
        }

        loadImage(imageFile);
        adapter = new SightSettingsAdapter(this, bow.getSightMarks());
        contentBinding.sightMarks.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        bow = buildBow();
        super.onSaveInstanceState(outState);
    }

    private void onAddSightSetting() {
        bow.getSightMarks().add(new SightMark());
        adapter.notifyItemInserted(bow.getSightMarks().size() - 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == SimpleDistanceSelector.SIMPLE_DISTANCE_REQUEST_CODE) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            final int index = intentData.getInt(SelectorBase.INDEX);
            final Parcelable parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
            bow.getSightMarks().get(index).distance = Parcels.unwrap(parcelable);
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
        bow.imageFile = getImageFile();
        bow.thumbnail = getThumbnail();
        return bow;
    }

    private static class SightSettingHolder extends DynamicItemHolder<SightMark> {

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
        public void onBind(SightMark sightMark, int position, Fragment fragment, View.OnClickListener removeListener) {
            item = sightMark;
            binding.distance.setOnActivityResultContext(fragment);
            binding.distance.setItemIndex(position);
            binding.distance.setItem(sightMark.distance);
            binding.sightSetting.setText(sightMark.value);
            binding.removeSightSetting.setOnClickListener(removeListener);
        }
    }

    private class SightSettingsAdapter extends DynamicItemAdapter<SightMark> {
        SightSettingsAdapter(Fragment fragment, List<SightMark> list) {
            super(fragment, list, R.string.sight_setting_removed);
        }

        @Override
        public DynamicItemHolder<SightMark> onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.item_sight_mark, parent, false);
            return new SightSettingHolder(v);
        }
    }
}
