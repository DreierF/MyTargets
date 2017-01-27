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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ItemSelectActivity;
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemAdapter;
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemHolder;
import de.dreier.mytargets.base.fragments.EditWithImageFragmentBase;
import de.dreier.mytargets.databinding.FragmentEditBowBinding;
import de.dreier.mytargets.databinding.ItemSightMarkBinding;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.BowImage;
import de.dreier.mytargets.shared.models.db.SightMark;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.views.selector.SelectorBase;
import de.dreier.mytargets.views.selector.SimpleDistanceSelector;
import icepick.State;

public class EditBowFragment extends EditWithImageFragmentBase<BowImage> {

    public static final String BOW_TYPE = "bow_type";
    @VisibleForTesting
    public static final String BOW_ID = "bow_id";

    @State(ParcelsBundler.class)
    Bow bow;
    private FragmentEditBowBinding contentBinding;
    private SightMarksAdapter adapter;

    public EditBowFragment() {
        super(R.drawable.recurve_bow, BowImage.class);
    }

    @NonNull
    public static IntentWrapper createIntent(EBowType bowType) {
        return new IntentWrapper(EditBowActivity.class)
                .with(EditBowFragment.BOW_TYPE, bowType.name());
    }

    @NonNull
    public static IntentWrapper editIntent(Bow bow) {
        return new IntentWrapper(EditBowActivity.class)
                .with(BOW_ID, bow.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                bow = Bow.get(bundle.getLong(BOW_ID));
            } else {
                // Set to default values
                bow = new Bow();
                bow.name = getString(R.string.my_bow);
                bow.type = bowType;
                bow.getSightMarks().add(new SightMark());
            }
            setImageFiles(bow.getImages());
        }
        ToolbarUtils.setTitle(this, bow.name);
        contentBinding.setBow(bow);

        loadImage(imageFile);
        adapter = new SightMarksAdapter(this, bow.getSightMarks());
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
    public void onSaveInstanceState(Bundle outState) {
        bow = buildBow();
        super.onSaveInstanceState(outState);
    }

    private void onAddSightSetting() {
        bow.getSightMarks().add(new SightMark());
        adapter.setList(bow.getSightMarks());
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
        bow.button = contentBinding.button.getText().toString();
        bow.string = contentBinding.string.getText().toString();
        bow.nockingPoint = contentBinding.nockingPoint.getText().toString();
        bow.letoffWeight = contentBinding.letoffWeight.getText().toString();
        bow.arrowRest = contentBinding.rest.getText().toString();
        bow.restHorizontalPosition = contentBinding.restHorizontalPosition.getText().toString();
        bow.restVerticalPosition = contentBinding.restVerticalPosition.getText().toString();
        bow.restStiffness = contentBinding.restStiffness.getText().toString();
        bow.camSetting = contentBinding.cam.getText().toString();
        bow.scopeMagnification = contentBinding.scopeMagnification.getText().toString();
        bow.images = getImageFiles();
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

    private class SightMarksAdapter extends DynamicItemAdapter<SightMark> {
        SightMarksAdapter(Fragment fragment, List<SightMark> list) {
            super(fragment, list, R.string.sight_setting_removed);
        }

        @Override
        public DynamicItemHolder<SightMark> onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.item_sight_mark, parent, false);
            return new SightSettingHolder(v);
        }
    }
}
