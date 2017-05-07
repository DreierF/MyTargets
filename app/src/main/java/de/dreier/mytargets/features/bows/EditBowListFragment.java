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

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.fragments.EditableListFragment;
import de.dreier.mytargets.databinding.FragmentBowsBinding;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.SlideInItemAnimator;

import static de.dreier.mytargets.shared.models.EBowType.BARE_BOW;
import static de.dreier.mytargets.shared.models.EBowType.COMPOUND_BOW;
import static de.dreier.mytargets.shared.models.EBowType.HORSE_BOW;
import static de.dreier.mytargets.shared.models.EBowType.LONG_BOW;
import static de.dreier.mytargets.shared.models.EBowType.RECURVE_BOW;
import static de.dreier.mytargets.shared.models.EBowType.YUMI;

public class EditBowListFragment extends EditableListFragment<Bow> {

    protected FragmentBowsBinding binding;

    public EditBowListFragment() {
        itemTypeSelRes = R.plurals.bow_selected;
        itemTypeDelRes = R.plurals.bow_deleted;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.fab.close(false);
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bows, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.full_divider));
        adapter = new BowAdapter(selector, this);
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(adapter);

        setFabClickListener(binding.fabBowRecurve, RECURVE_BOW);
        setFabClickListener(binding.fabBowCompound, COMPOUND_BOW);
        setFabClickListener(binding.fabBowBare, BARE_BOW);
        setFabClickListener(binding.fabBowLong, LONG_BOW);
        setFabClickListener(binding.fabBowHorse, HORSE_BOW);
        setFabClickListener(binding.fabBowYumi, YUMI);

        return binding.getRoot();
    }

    public void setFabClickListener(FloatingActionButton fab, EBowType bowType) {
        fab.setOnClickListener(view -> EditBowFragment
                .createIntent(bowType)
                .withContext(this)
                .fromFab(fab, R.color.fabBow, bowType.getDrawable())
                .start());
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        List<Bow> bows = Bow.getAll();
        return () -> adapter.setList(bows);
    }

    @Override
    protected void onEdit(Bow item) {
        EditBowFragment.editIntent(item).withContext(this).start();
    }

    @Override
    protected void onItemSelected(Bow item) {
        EditBowFragment.editIntent(item).withContext(this).start();
    }
}
