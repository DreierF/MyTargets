/*
 * Copyright (C) 2017 Florian Dreier
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

package de.dreier.mytargets.features.bows;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.android.state.State;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ItemSelectActivity;
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemAdapter;
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemHolder;
import de.dreier.mytargets.base.fragments.EditWithImageFragmentBase;
import de.dreier.mytargets.databinding.FragmentEditBowBinding;
import de.dreier.mytargets.databinding.ItemSightMarkBinding;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.BowImage;
import de.dreier.mytargets.shared.models.db.SightMark;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.views.selector.SelectorBase;
import de.dreier.mytargets.views.selector.SimpleDistanceSelector;

public class EditBowFragment extends EditWithImageFragmentBase<BowImage> {

    public static final String BOW_TYPE = "bow_type";
    @VisibleForTesting
    public static final String BOW_ID = "bow_id";

    @Nullable
    @State
    Bow bow;

    @State
    ArrayList<SightMark> sightMarks;

    private FragmentEditBowBinding contentBinding;
    private SightMarksAdapter adapter;

    public EditBowFragment() {
        super(R.drawable.recurve_bow, BowImage.class);
    }

    @NonNull
    public static IntentWrapper createIntent(@NonNull EBowType bowType) {
        return new IntentWrapper(EditBowActivity.class)
                .with(EditBowFragment.BOW_TYPE, bowType.name());
    }

    @NonNull
    public static IntentWrapper editIntent(long bowId) {
        return new IntentWrapper(EditBowActivity.class)
                .with(BOW_ID, bowId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        contentBinding = FragmentEditBowBinding.inflate(inflater, binding.content, true);
        contentBinding.addButton.setOnClickListener(v -> onAddSightSetting());
        contentBinding.moreFields.setOnClickListener(v -> contentBinding.setShowAll(true));

        EBowType bowType = EBowType
                .valueOf(getArguments().getString(BOW_TYPE, EBowType.RECURVE_BOW.name()));

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey(BOW_ID)) {
                // Load data from database
                bow = Bow.Companion.get(bundle.getLong(BOW_ID));
                sightMarks = bow.loadSightMarks();
            } else {
                // Set to default values
                bow = new Bow();
                bow.setName(getString(R.string.my_bow));
                bow.setType(bowType);
                sightMarks = new ArrayList<>();
                sightMarks.add(new SightMark());
            }
            setImageFiles(bow.loadImages());
        }
        ToolbarUtils.setTitle(this, bow.getName());
        contentBinding.setBow(bow);

        loadImage(imageFile);
        adapter = new SightMarksAdapter(this, sightMarks);
        contentBinding.sightMarks.setAdapter(adapter);
        contentBinding.sightMarks.setNestedScrollingEnabled(false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        contentBinding.rootView.requestFocus();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        bow = buildBow();
        super.onSaveInstanceState(outState);
    }

    private void onAddSightSetting() {
        sightMarks.add(new SightMark());
        adapter.setList(sightMarks);
        adapter.notifyItemInserted(sightMarks.size() - 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK &&
                requestCode == SimpleDistanceSelector.Companion.getSIMPLE_DISTANCE_REQUEST_CODE()) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            final int index = intentData.getInt(SelectorBase.INDEX);
            final Dimension parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
            sightMarks.get(index).setDistance(parcelable);
            adapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onSave() {
        super.onSave();
        buildBow().save();
        finish();
    }

    @Nullable
    private Bow buildBow() {
        bow.setName(contentBinding.name.getText().toString());
        bow.setBrand(contentBinding.brand.getText().toString());
        bow.setSize(contentBinding.size.getText().toString());
        bow.setBraceHeight(contentBinding.braceHeight.getText().toString());
        bow.setTiller(contentBinding.tiller.getText().toString());
        bow.setLimbs(contentBinding.limbs.getText().toString());
        bow.setSight(contentBinding.sight.getText().toString());
        bow.setDrawWeight(contentBinding.drawWeight.getText().toString());
        bow.setStabilizer(contentBinding.stabilizer.getText().toString());
        bow.setClicker(contentBinding.clicker.getText().toString());
        bow.setDescription(contentBinding.description.getText().toString());
        bow.setButton(contentBinding.button.getText().toString());
        bow.setString(contentBinding.string.getText().toString());
        bow.setNockingPoint(contentBinding.nockingPoint.getText().toString());
        bow.setLetoffWeight(contentBinding.letoffWeight.getText().toString());
        bow.setArrowRest(contentBinding.rest.getText().toString());
        bow.setRestHorizontalPosition(contentBinding.restHorizontalPosition.getText().toString());
        bow.setRestVerticalPosition(contentBinding.restVerticalPosition.getText().toString());
        bow.setRestStiffness(contentBinding.restStiffness.getText().toString());
        bow.setCamSetting(contentBinding.cam.getText().toString());
        bow.setScopeMagnification(contentBinding.scopeMagnification.getText().toString());
        bow.setImages(getImageFiles());
        bow.thumbnail = getThumbnail();
        bow.setSightMarks(sightMarks);
        return bow;
    }

    private static class SightSettingHolder extends DynamicItemHolder<SightMark> {

        private final ItemSightMarkBinding binding;

        SightSettingHolder(@NonNull View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
            binding.sightSetting.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(@NonNull CharSequence s, int i, int i1, int i2) {
                    item.setValue(s.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        @Override
        public void onBind(@NonNull SightMark sightMark, int position, @NonNull Fragment fragment, View.OnClickListener removeListener) {
            item = sightMark;
            binding.distance.setOnActivityResultContext(fragment);
            binding.distance.setItemIndex(position);
            binding.distance.setItem(sightMark.getDistance());
            binding.sightSetting.setText(sightMark.getValue());
            binding.removeSightSetting.setOnClickListener(removeListener);
        }
    }

    private class SightMarksAdapter extends DynamicItemAdapter<SightMark> {
        SightMarksAdapter(@NonNull Fragment fragment, List<SightMark> list) {
            super(fragment, list, R.string.sight_setting_removed);
        }

        @NonNull
        @Override
        public DynamicItemHolder<SightMark> onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.item_sight_mark, parent, false);
            return new SightSettingHolder(v);
        }
    }
}
