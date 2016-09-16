/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemStandardRoundBinding;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.DataLoaderBase.BackgroundAction;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class StandardRoundListFragment extends SelectItemFragment<StandardRound>
        implements View.OnClickListener, SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<List<StandardRound>> {

    private static final int NEW_STANDARD_ROUND = 1;
    private static final String KEY_QUERY = "query";
    private FragmentListBinding binding;

    private StandardRound currentSelection;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        mAdapter = new StandardRoundAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);
        binding.fab.setOnClickListener(this);
        useDoubleClickSelection = true;
        ToolbarUtils.showUpAsX(this);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentSelection = Parcels.unwrap(getArguments().getParcelable(ITEM));
    }

    @Override
    public Loader<List<StandardRound>> onCreateLoader(int id, Bundle args) {
        StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource();
        final BackgroundAction<StandardRound> action;
        if (args != null && args.containsKey(KEY_QUERY)) {
            String query = args.getString(KEY_QUERY);
            action = () -> standardRoundDataSource.getAllSearch(query);
        } else {
            action = standardRoundDataSource::getAll;
        }
        return new DataLoader<>(getContext(), standardRoundDataSource, action);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchView != null) {
            Bundle args = new Bundle();
            args.putString(KEY_QUERY, searchView.getQuery().toString());
            getLoaderManager().restartLoader(0, args, this);
        } else {
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public void onLoadFinished(Loader<List<StandardRound>> loader, List<StandardRound> data) {
        mAdapter.setList(data);
        int position = data.indexOf(currentSelection);
        // Test if our currentSelection has been deleted
        if (position == -1 && new StandardRoundDataSource().get(currentSelection.getId()) == null) {
            currentSelection = data.size() > 0 ? data.get(0) : new StandardRoundDataSource()
                    .get(32);
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

    @Override
    public void onLoaderReset(Loader<List<StandardRound>> loader) {

    }

    @Override
    public void onClick(SelectableViewHolder holder, StandardRound mItem) {
        if (mItem == null) {
            return;
        }
        currentSelection = mItem;
        super.onClick(holder, mItem);
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        StandardRound item = (StandardRound) holder.getItem();
        if (item.club == StandardRoundFactory.CUSTOM && item.usages == 0) {
            EditStandardRoundFragment
                    .editIntent(this, item)
                    .startForResult(NEW_STANDARD_ROUND);
        } else {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.use_as_template)
                    .content(R.string.create_copy)
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.cancel)
                    .onPositive((dialog1, which1) -> EditStandardRoundFragment
                                    .editIntent(this, item)
                                    .startForResult(NEW_STANDARD_ROUND))
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
        EditStandardRoundFragment.createIntent(this)
                .fromFab(binding.fab)
                .startForResult(NEW_STANDARD_ROUND);
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

    private class StandardRoundAdapter extends ListAdapterBase<StandardRound> {
        StandardRoundAdapter(Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            return new ViewHolder(
                    DataBindingUtil.inflate(inflater, R.layout.item_standard_round, parent, false));
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
            binding.name.setText(mItem.name);

            if (mItem.equals(currentSelection)) {
                binding.image.setVisibility(View.VISIBLE);
                binding.details.setVisibility(View.VISIBLE);
                binding.details.setText(mItem.getDescription(getActivity()));
                binding.image.setImageDrawable(mItem.getTargetDrawable());
            } else {
                binding.image.setVisibility(View.GONE);
                binding.details.setVisibility(View.GONE);
            }
        }
    }
}
