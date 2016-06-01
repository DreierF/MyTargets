/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.DataLoaderBase.BackgroundAction;
import de.dreier.mytargets.utils.SelectableViewHolder;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

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

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private ArrayList<StandardRound> list;
    private RadioGroup location;
    private RadioGroup unit;
    private RadioGroup typ;
    private StandardRound currentSelection;
    private StandardRoundDataSource standardRoundDataSource;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        useDoubleClickSelection = true;
        currentSelection = Parcels.unwrap(getArguments().getParcelable(ITEM));
        list = new StandardRoundDataSource(getContext()).getAll();
        if (!list.contains(currentSelection)) {
            list.add(currentSelection);
        }
        setHasOptionsMenu(true);
        initFilter();
    }

    @Override
    public Loader<List<StandardRound>> onCreateLoader(int id, Bundle args) {
        standardRoundDataSource = new StandardRoundDataSource(getContext());
        final BackgroundAction<StandardRound> action;
        if (args.containsKey(KEY_QUERY)) {
            String query = args.getString(KEY_QUERY);
            action = () -> standardRoundDataSource.getAllSearch(query);
        } else {
            int clubsFilter = args.getInt(KEY_CLUB_FILTER);
            boolean indoor = args.getBoolean(KEY_INDOOR);
            boolean isMetric = args.getBoolean(KEY_METRIC);
            int checked = args.getInt(KEY_CHECKED);
            action = () -> standardRoundDataSource.getAllFiltered(clubsFilter, indoor, isMetric, checked);
        }
        return new DataLoader<>(getContext(), standardRoundDataSource, action);
    }

    @Override
    public void onLoadFinished(Loader<List<StandardRound>> loader, List<StandardRound> data) {
        setList(data, new StandardRoundAdapter());
        int position = data.indexOf(currentSelection);
        if (position > -1) {
            mSelector.setSelected(position, currentSelection.getId(), true);
            mRecyclerView.scrollToPosition(position);
        } else {
            mSelector.clearSelections();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<StandardRound>> loader) {

    }

    private void initFilter() {
        location = (RadioGroup) rootView.findViewById(R.id.location);
        unit = (RadioGroup) rootView.findViewById(R.id.unit);
        typ = (RadioGroup) rootView.findViewById(R.id.round_typ);
        getClubs();

        // Set default values
        RoundTemplate firstRound = currentSelection.getRounds().get(0);
        setLocation();
        setMeasurementType(firstRound);
        setRoundType(firstRound);
        setInitialFilterMask();
        updateFilter();

        // Listen for filter setting changes
        for (CheckBox club : clubs) {
            club.setOnCheckedChangeListener((buttonView, isChecked) -> updateFilter());
        }
        location.setOnCheckedChangeListener((group, checkedId) -> updateFilter());
        unit.setOnCheckedChangeListener((group, checkedId) -> updateFilter());
        typ.setOnCheckedChangeListener((group, checkedId) -> updateFilter());
    }

    private void setLocation() {
        RadioButton outdoor = (RadioButton) rootView.findViewById(R.id.outdoor);
        RadioButton indoor = (RadioButton) rootView.findViewById(R.id.indoor);
        indoor.setChecked(currentSelection.indoor);
        outdoor.setChecked(!currentSelection.indoor);
    }

    private void setMeasurementType(RoundTemplate firstRound) {
        RadioButton metric = (RadioButton) rootView.findViewById(R.id.metric);
        RadioButton imperial = (RadioButton) rootView.findViewById(R.id.imperial);
        if (firstRound.distance.unit.equals(Dimension.METER)) {
            metric.setChecked(true);
        } else {
            imperial.setChecked(true);
        }
    }

    private void setRoundType(RoundTemplate firstRound) {
        RadioButton target = (RadioButton) rootView.findViewById(R.id.target);
        RadioButton field = (RadioButton) rootView.findViewById(R.id.field);
        RadioButton threeD = (RadioButton) rootView.findViewById(R.id.three_d);
        if (firstRound.target.getModel().isFieldTarget()) {
            field.setChecked(true);
        } else if (firstRound.target.getModel().is3DTarget()) {
            threeD.setChecked(true);
        } else {
            target.setChecked(true);
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
        clubs[0] = (CheckBox) rootView.findViewById(R.id.asa);
        clubs[1] = (CheckBox) rootView.findViewById(R.id.aussie);
        clubs[2] = (CheckBox) rootView.findViewById(R.id.archerygb);
        clubs[3] = (CheckBox) rootView.findViewById(R.id.ifaa);
        clubs[4] = (CheckBox) rootView.findViewById(R.id.nasp);
        clubs[5] = (CheckBox) rootView.findViewById(R.id.nfaa);
        clubs[6] = (CheckBox) rootView.findViewById(R.id.nfas);
        clubs[7] = (CheckBox) rootView.findViewById(R.id.wa);
        clubs[8] = (CheckBox) rootView.findViewById(R.id.custom);
    }

    private void updateFilter() {
        int filter = getFilter();
        SettingsManager.setClubFilter(filter);

        Bundle args = new Bundle();
        args.putBoolean(KEY_INDOOR, location.getCheckedRadioButtonId() == R.id.indoor);
        args.putBoolean(KEY_METRIC, unit.getCheckedRadioButtonId() == R.id.metric);
        args.putInt(KEY_CHECKED, typ.getCheckedRadioButtonId());
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
    protected int getLayoutResource() {
        return R.layout.fragment_standard_round_selection;
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
        if (item.club == StandardRoundFactory.CUSTOM) {
            startEditStandardRound(item);
        } else {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.use_as_template)
                    .content(R.string.create_copy)
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.cancel)
                    .onPositive((dialog1, which1) -> {
                        startEditStandardRound(item);
                    })
                    .show();
        }
    }

    private void startEditStandardRound(StandardRound item) {
        Intent i = new Intent(getActivity(),
                SimpleFragmentActivity.EditStandardRoundActivity.class);
        i.putExtra(ITEM, Parcels.wrap(item));
        startActivityForResult(i, NEW_STANDARD_ROUND);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_filter, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == R.id.action_filter) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(
                new Intent(getActivity(), SimpleFragmentActivity.EditStandardRoundActivity.class),
                NEW_STANDARD_ROUND);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
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

    protected class StandardRoundAdapter extends NowListAdapter<StandardRound> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_standard_round, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends SelectableViewHolder<StandardRound> {
        private final TextView mName;
        private final ImageView mImage;
        private final TextView mDetails;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, StandardRoundFragment.this);
            mImage = (ImageView) itemView.findViewById(R.id.image);
            mName = (TextView) itemView.findViewById(android.R.id.text1);
            mDetails = (TextView) itemView.findViewById(android.R.id.text2);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);

            if (mItem.equals(currentSelection)) {
                mImage.setVisibility(View.VISIBLE);
                mDetails.setVisibility(View.VISIBLE);
                mDetails.setText(mItem.getDescription(getActivity()));
                mImage.setImageDrawable(mItem.getTargetDrawable());
            } else {
                mImage.setVisibility(View.GONE);
                mDetails.setVisibility(View.GONE);
            }
        }
    }
}
