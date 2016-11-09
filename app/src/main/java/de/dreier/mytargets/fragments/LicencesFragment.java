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

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import me.oriley.homage.Homage;
import me.oriley.homage.recyclerview.HomageAdapter;
import me.oriley.homage.recyclerview.HomageView;

public final class LicencesFragment extends Fragment {

    private static final String KEY_LAYOUT_MANAGER_STATE = "layoutManagerState";

    private FragmentListBinding binding;
    private RecyclerView.LayoutManager layoutManager;

    @NonNull
    private RecyclerView.Adapter createAdapter() {
        Homage homage = new Homage(getActivity(), R.raw.licences);

        // Adds a custom license definition to enable matching in your JSON list
        homage.addLicense("epl", R.string.license_epl_name, R.string.license_epl_url,
                R.string.license_epl_description);

        homage.refreshLibraries();

        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.full_divider));
        binding.fab.setVisibility(View.GONE);
        return new HomageAdapter(homage, HomageView.ExtraInfoMode.EXPANDABLE, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter = createAdapter();
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(adapter);
        layoutManager = binding.recyclerView.getLayoutManager();

        if (savedInstanceState != null) {
            Parcelable layoutState = savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE);
            layoutManager.onRestoreInstanceState(layoutState);
        }
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (layoutManager != null) {
            outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, layoutManager.onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }
}
