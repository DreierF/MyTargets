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
import android.support.design.widget.FloatingActionButton;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.fragments.EditableListFragment;
import de.dreier.mytargets.base.fragments.ItemActionModeCallback;
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

    @NonNull
    static SparseArray<EBowType> bowTypeMap = new SparseArray<>();

    static {
        bowTypeMap.put(R.id.fabBowRecurve, RECURVE_BOW);
        bowTypeMap.put(R.id.fabBowCompound, COMPOUND_BOW);
        bowTypeMap.put(R.id.fabBowBare, BARE_BOW);
        bowTypeMap.put(R.id.fabBowLong, LONG_BOW);
        bowTypeMap.put(R.id.fabBowHorse, HORSE_BOW);
        bowTypeMap.put(R.id.fabBowYumi, YUMI);
    }

    public EditBowListFragment() {
        itemTypeDelRes = R.plurals.bow_deleted;
        actionModeCallback = new ItemActionModeCallback(this, selector,
                R.plurals.bow_selected);
        actionModeCallback.setEditCallback(this::onEdit);
        actionModeCallback.setDeleteCallback(this::onDelete);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.fabSpeedDial.closeMenu();
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

        binding.fabSpeedDial.setMenuListener(menuItem -> {
            int itemId = menuItem.getItemId();
            EBowType bowType = bowTypeMap.get(itemId);
            FloatingActionButton fab = binding.fabSpeedDial.getFabFromMenuId(itemId);
            EditBowFragment
                    .createIntent(bowType)
                    .withContext(EditBowListFragment.this)
                    .fromFab(fab, R.color.fabBow, bowType.getDrawable())
                    .start();
            return false;
        });

        return binding.getRoot();
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        List<Bow> bows = Bow.getAll();
        return () -> {
            adapter.setList(bows);
            binding.emptyState.getRoot().setVisibility(bows.isEmpty() ? View.VISIBLE : View.GONE);
        };
    }

    protected void onEdit(long itemId) {
        EditBowFragment.editIntent(itemId)
                .withContext(this)
                .start();
    }

    @Override
    protected void onItemSelected(@NonNull Bow item) {
        EditBowFragment.editIntent(item.getId()).withContext(this).start();
    }
}
