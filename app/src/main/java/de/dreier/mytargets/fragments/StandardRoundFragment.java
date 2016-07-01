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
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.parceler.Parcels;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.EditStandardRoundActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.databinding.FragmentStandardRoundBinding;
import de.dreier.mytargets.databinding.ItemStandardRoundBinding;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.FlowDataLoader;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.ToolbarUtils;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;
import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;
import static de.dreier.mytargets.utils.ActivityUtils.startActivityAnimated;

public class StandardRoundFragment extends SelectItemFragment<StandardRound>
        implements View.OnClickListener, SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<List<StandardRound>> {

    private static final int NEW_STANDARD_ROUND = 1;
    private static final String KEY_QUERY = "query";
    private static final String KEY_INDOOR = "indoor";
    private static final String KEY_METRIC = "metric";
    private static final String KEY_CHECKED = "checked";
    private static final String KEY_CLUB_FILTER = "club_filter";
    private final CheckBox[] clubs = new CheckBox[9];
    protected FragmentStandardRoundBinding binding;

    private StandardRound currentSelection;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_standard_round, container, false);
        binding.recyclerView.setHasFixedSize(true);
        mAdapter = new StandardRoundAdapter(getContext());
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
        initFilter();
    }

    @Override
    public Loader<List<StandardRound>> onCreateLoader(int id, Bundle args) {
        final FlowDataLoader.BackgroundAction<StandardRound> action;
        if (args == null) {
            action = StandardRound::getAll;
        } else {
            String query = args.getString(KEY_QUERY);
            action = () -> StandardRound.getAllSearch(query);
        }
        return new FlowDataLoader<>(getContext(), action);
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

    @Override
    public void onLoaderReset(Loader<List<StandardRound>> loader) {

    }

    private void initFilter() {
        getClubs();

        // Set default values
        RoundTemplate firstRound = currentSelection.rounds.get(0);
        setLocation();
        setMeasurementType(firstRound);
        setRoundType(firstRound);
        setInitialFilterMask();
        updateFilter();

        // Listen for filter setting changes
        for (CheckBox club : clubs) {
            club.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilter());
        }
        binding.location.setOnCheckedChangeListener((group, checkedId) -> updateFilter());
        binding.unit.setOnCheckedChangeListener((group, checkedId) -> updateFilter());
        binding.roundTyp.setOnCheckedChangeListener((group, checkedId) -> updateFilter());
    }

    private void setLocation() {
        binding.indoor.setChecked(currentSelection.indoor);
        binding.outdoor.setChecked(!currentSelection.indoor);
    }

    private void setMeasurementType(RoundTemplate firstRound) {
        if (METER.equals(firstRound.distance.unit)) {
            binding.metric.setChecked(true);
        } else {
            binding.imperial.setChecked(true);
        }
    }

    private void setRoundType(RoundTemplate firstRound) {
        if (firstRound.target.getModel().isFieldTarget()) {
            binding.field.setChecked(true);
        } else if (firstRound.target.getModel().is3DTarget()) {
            binding.threeD.setChecked(true);
        } else {
            binding.target.setChecked(true);
        }
    }

    private void setInitialFilterMask() {
        int filterMask = SettingsManager.getClubFilter();
        filterMask |= currentSelection.club;
        for (int i = 0; i < clubs.length; i++) {
            clubs[i].setChecked((1 << i & filterMask) != 0);
        }
    }

    private void getClubs() {
        clubs[0] = binding.asa;
        clubs[1] = binding.aussie;
        clubs[2] = binding.archerygb;
        clubs[3] = binding.ifaa;
        clubs[4] = binding.nasp;
        clubs[5] = binding.nfaa;
        clubs[6] = binding.nfas;
        clubs[7] = binding.wa;
        clubs[8] = binding.custom;
    }

    private void updateFilter() {
        int filter = getFilter();
        SettingsManager.setClubFilter(filter);

        Bundle args = new Bundle();
        args.putBoolean(KEY_INDOOR, binding.location.getCheckedRadioButtonId() == R.id.indoor);
        args.putBoolean(KEY_METRIC, binding.unit.getCheckedRadioButtonId() == R.id.metric);
        args.putInt(KEY_CHECKED, binding.roundTyp.getCheckedRadioButtonId());
        args.putInt(KEY_CLUB_FILTER, filter);
        getLoaderManager().restartLoader(0, args, this);
    }

    private int getFilter() {
        int filter = 0;
        for (int i = 0; i < clubs.length; i++) {
            filter |= (clubs[i].isChecked() ? 1 : 0) << i;
        }
        return filter;
    }

    @Override
    public void onClick(SelectableViewHolder holder, StandardRound mItem) {
        super.onClick(holder, mItem);
        if (mItem == null) {
            return;
        }
        currentSelection = mItem;
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        StandardRound item = (StandardRound) holder.getItem();
        if (item.club == StandardRoundFactory.CUSTOM && item.usages == 0) {
            startEditStandardRound(item);
        } else {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.use_as_template)
                    .content(R.string.create_copy)
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.cancel)
                    .onPositive((dialog1, which1) -> startEditStandardRound(item))
                    .show();
        }
    }

    private void startEditStandardRound(StandardRound item) {
        startActivityAnimated(getActivity(), EditStandardRoundActivity.class, NEW_STANDARD_ROUND, ITEM, Parcels.wrap(item));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_filter, menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == R.id.action_filter) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        startActivityAnimated(getActivity(), EditStandardRoundActivity.class, NEW_STANDARD_ROUND);
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

    private class StandardRoundAdapter extends NowListAdapter<StandardRound> {
        StandardRoundAdapter(Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            return new ViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_standard_round, parent, false));
        }
    }

    public class ViewHolder extends SelectableViewHolder<StandardRound> {
        private final ItemStandardRoundBinding binding;

        public ViewHolder(ItemStandardRoundBinding binding) {
            super(binding.getRoot(), mSelector, StandardRoundFragment.this);
            this.binding = binding;
        }

        @Override
        public void bindCursor() {
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
