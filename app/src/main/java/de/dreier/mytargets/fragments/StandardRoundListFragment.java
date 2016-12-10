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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
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

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ItemStandardRoundBinding;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class StandardRoundListFragment extends SelectPureListItemFragmentBase<StandardRound>
        implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static final int NEW_STANDARD_ROUND = 1;
    private static final String KEY_QUERY = "query";

    private StandardRound currentSelection;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding.recyclerView.setHasFixedSize(false);
        binding.fab.setOnClickListener(this);
        useDoubleClickSelection = true;
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentSelection = Parcels.unwrap(getArguments().getParcelable(ITEM));
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        List<StandardRound> data;
        if (args != null && args.containsKey(KEY_QUERY)) {
            data = StandardRound.getAll();
        } else {
            String query = args.getString(KEY_QUERY);
            data = StandardRound.getAllSearch(query);
        }
        return new LoaderUICallback() {
            @Override
            public void applyData() {
                mAdapter.setList(data);
                int position = data.indexOf(currentSelection);
                // Test if our currentSelection has been deleted
                if (position == -1 && StandardRound.get(currentSelection.getId()) == null) {
                    currentSelection = data.size() > 0 ? data.get(0) : StandardRound.get(32L);
                    Intent dataIntent = new Intent();
                    dataIntent.putExtra(ITEM, Parcels.wrap(currentSelection));
                    getActivity().setResult(Activity.RESULT_OK, dataIntent);
                }
                if (position > -1) {
                    binding.recyclerView.post(() -> {
                        mSelector.setSelected(position, currentSelection.getId(), true);
                        LinearLayoutManager manager = (LinearLayoutManager) binding.recyclerView
                                .getLayoutManager();
                        int first = manager.findFirstCompletelyVisibleItemPosition();
                        int last = manager.findLastCompletelyVisibleItemPosition();
                        if (first > position || last < position) {
                            binding.recyclerView.scrollToPosition(position);
                        }
                    });
                } else {
                    mSelector.clearSelections();
                }

            }
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
        if (item.club == StandardRoundFactory.CUSTOM /*&& item.usages == 0*/) {
            EditStandardRoundFragment.editIntent(item)
                    .withContext(this).forResult(NEW_STANDARD_ROUND)
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
    public void onClick(View v) {
        EditStandardRoundFragment.createIntent()
                .withContext(this)
                .fromFab(binding.fab).forResult(NEW_STANDARD_ROUND)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == NEW_STANDARD_ROUND) {
            getActivity().setResult(resultCode, data);
            getActivity().onBackPressed();
        }
    }

    @Override
    protected StandardRound onSave() {
        return currentSelection;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Bundle args = new Bundle();
        args.putString(KEY_QUERY, query);
        getLoaderManager().restartLoader(0, args, this);
        return false;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(
                DataBindingUtil.inflate(inflater, R.layout.item_standard_round, parent, false));
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
