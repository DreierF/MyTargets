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

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.parceler.Parcels;

import java.util.List;
import java.util.Map;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.HeaderListAdapter;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemStandardRoundBinding;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static android.app.Activity.RESULT_OK;
import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class StandardRoundListFragment extends SelectItemFragmentBase<StandardRound>
        implements SearchView.OnQueryTextListener {

    private static final int NEW_STANDARD_ROUND = 1;
    private static final int EDIT_STANDARD_ROUND = 2;
    private static final String KEY_QUERY = "query";

    private StandardRound currentSelection;
    private SearchView searchView;

    protected FragmentListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        Map<Long, Integer> usedRounds = SettingsManager.getStandardRoundsLastUsed();
        mAdapter = new StandardRoundListAdapter(getContext(), usedRounds);
        binding.recyclerView.setAdapter(mAdapter);
        binding.fab.setVisibility(View.GONE);
        ToolbarUtils.showUpAsX(this);
        binding.recyclerView.setHasFixedSize(false);
        binding.fab.setVisibility(View.VISIBLE);
        binding.fab.setOnClickListener(view -> EditStandardRoundFragment.createIntent()
                .withContext(StandardRoundListFragment.this)
                .fromFab(binding.fab).forResult(NEW_STANDARD_ROUND)
                .start());
        useDoubleClickSelection = true;
        setHasOptionsMenu(true);
        currentSelection = Parcels.unwrap(getArguments().getParcelable(ITEM));
        return binding.getRoot();
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        List<StandardRound> data;
        if (args != null && args.containsKey(KEY_QUERY)) {
            String query = args.getString(KEY_QUERY);
            data = StandardRound.getAllSearch(query);
        } else {
            data = StandardRound.getAll();
        }
        return () -> {
            mAdapter.setList(data);
            selectItem(binding.recyclerView, currentSelection);
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchView != null) {
            Bundle args = new Bundle();
            args.putString(KEY_QUERY, searchView.getQuery().toString());
            reloadData(args);
        } else {
            reloadData();
        }
    }

    @Override
    public void onClick(SelectableViewHolder<StandardRound> holder, StandardRound mItem) {
        if (mItem == null) {
            return;
        }
        currentSelection = mItem;
        super.onClick(holder, mItem);
    }

    @Override
    public void onLongClick(SelectableViewHolder<StandardRound> holder) {
        StandardRound item = holder.getItem();
        if (item.club == StandardRoundFactory.CUSTOM) {
            EditStandardRoundFragment.editIntent(item)
                    .withContext(this)
                    .forResult(EDIT_STANDARD_ROUND)
                    .start();
        } else {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.use_as_template)
                    .content(R.string.create_copy)
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.cancel)
                    .onPositive((dialog1, which1) -> EditStandardRoundFragment
                            .editIntent(item)
                            .withContext(this)
                            .forResult(NEW_STANDARD_ROUND)
                            .start())
                    .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        // Set on click listener
        closeButton.setOnClickListener(v -> {
            EditText et = (EditText) searchView.findViewById(R.id.search_src_text);
            et.setText("");
            searchView.setQuery("", false);
            searchView.onActionViewCollapsed();
            searchItem.collapseActionView();
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Bundle args = new Bundle();
        args.putString(KEY_QUERY, query);
        reloadData(args);
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == NEW_STANDARD_ROUND) {
            getActivity().setResult(resultCode, data);
            finish();
        } else if (requestCode == EDIT_STANDARD_ROUND) {
            if (resultCode == RESULT_OK) {
                currentSelection = Parcels.unwrap(data.getParcelableExtra(ITEM));
                reloadData();
            } else if (resultCode == EditStandardRoundFragment.RESULT_STANDARD_ROUND_DELETED) {
                currentSelection = StandardRound.get(32L);
                reloadData();
            }
        }
    }

    @Override
    protected StandardRound onSave() {
        return currentSelection;
    }

    private class StandardRoundListAdapter extends HeaderListAdapter<StandardRound> {
        StandardRoundListAdapter(Context context, Map<Long, Integer> usedIds) {
            super(child -> {
                if (usedIds.containsKey(child.getId())) {
                    return new SimpleHeader(0L, context.getString(R.string.recently_used));
                } else {
                    return new SimpleHeader(1L, "");
                }
            }, (r1, r2) -> {
                Integer usagesR1 = usedIds.get(r1.getId());
                Integer usagesR2 = usedIds.get(r2.getId());
                if (usagesR1 == null) {
                    usagesR1 = 0;
                }
                if (usagesR2 == null) {
                    usagesR2 = 0;
                }
                final int i = usagesR1.compareTo(usagesR2);
                return i == 0 ? r1.getName().compareTo(r2.getName()) : i;
            });
        }

        @Override
        protected ViewHolder getSecondLevelViewHolder(ViewGroup parent) {
            return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_standard_round, parent, false));
        }
    }

    public class ViewHolder extends SelectableViewHolder<StandardRound> {
        private final ItemStandardRoundBinding binding;

        public ViewHolder(ItemStandardRoundBinding binding) {
            super(binding.getRoot(), mSelector, StandardRoundListFragment.this);
            this.binding = binding;
        }

        @Override
        public void bindItem() {
            binding.name.setText(item.name);

            if (item.equals(currentSelection)) {
                binding.image.setVisibility(View.VISIBLE);
                binding.details.setVisibility(View.VISIBLE);
                binding.details.setText(item.getDescription(getActivity()));
                binding.image.setImageDrawable(item.getTargetDrawable());
            } else {
                binding.image.setVisibility(View.GONE);
                binding.details.setVisibility(View.GONE);
            }
        }
    }
}
