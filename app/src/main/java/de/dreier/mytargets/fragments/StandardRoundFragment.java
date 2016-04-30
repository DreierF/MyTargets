/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.targets.TargetModelBase;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.SelectableViewHolder;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class StandardRoundFragment extends SelectItemFragment<StandardRound> implements View.OnClickListener {

    private static final int NEW_STANDARD_ROUND = 1;
    private final CheckBox[] clubs = new CheckBox[9];
    private ArrayList<StandardRound> list;
    private RadioGroup location;
    private RadioGroup unit;
    private RadioGroup typ;
    private StandardRound currentSelection;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSelector.setSelectable(true);
        currentSelection = Parcels.unwrap(getArguments().getParcelable(ITEM));
        list = new StandardRoundDataSource(getContext()).getAll();
        if (!list.contains(currentSelection)) {
            list.add(currentSelection);
        }
        setHasOptionsMenu(true);
        initFilter();
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int filterMask = prefs.getInt("filter_club", 0x1FF);
        filterMask |= currentSelection.club;
        for (int i = 0; i < clubs.length; i++) {
            clubs[i].setChecked((1 << i & filterMask) != 0);
        }
    }

    private void getClubs() {
        clubs[0] = (CheckBox) rootView.findViewById(R.id.asa);
        clubs[1] = (CheckBox) rootView.findViewById(R.id.aussie);
        clubs[2] = (CheckBox) rootView.findViewById(R.id.gnas);
        clubs[3] = (CheckBox) rootView.findViewById(R.id.ifaa);
        clubs[4] = (CheckBox) rootView.findViewById(R.id.nasp);
        clubs[5] = (CheckBox) rootView.findViewById(R.id.nfaa);
        clubs[6] = (CheckBox) rootView.findViewById(R.id.nfas);
        clubs[7] = (CheckBox) rootView.findViewById(R.id.wa);
        clubs[8] = (CheckBox) rootView.findViewById(R.id.custom);
    }

    private void updateFilter() {
        int filter = getFilter();
        List<StandardRound> displayList = getFilteredStandardRound(filter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor e = prefs.edit();
        e.putInt("filter_club", filter);
        e.apply();

        setList(displayList, new StandardRoundAdapter());
        int position = displayList.indexOf(currentSelection);
        if (position > -1) {
            mSelector.setSelected(position, currentSelection.getId(), true);
            mRecyclerView.scrollToPosition(position);
        } else {
            mSelector.clearSelections();
        }
    }

    private List<StandardRound> getFilteredStandardRound(int filter) {
        ArrayList<StandardRound> displayList = new ArrayList<>();
        boolean indoor = location.getCheckedRadioButtonId() == R.id.indoor;
        boolean isMetric = unit.getCheckedRadioButtonId() == R.id.metric;
        String unitDistance = isMetric ? Distance.METER : Distance.YARDS;
        for (StandardRound r : list) {
            ArrayList<RoundTemplate> rounds = r.getRounds();
            if (rounds.size() > 0 && ((r.club & filter) != 0 ||
                    r.name.startsWith("NFAA/IFAA") && (filter & StandardRoundFactory.IFAA) != 0) &&
                    rounds.get(0).distance.unit.equals(unitDistance) &&
                    r.indoor == indoor) {
                int checked = typ.getCheckedRadioButtonId();
                TargetModelBase target = rounds.get(0).target.getModel();
                if ((checked != R.id.field || target.isFieldTarget()) &&
                        (checked != R.id.three_d || target.is3DTarget())) {
                    displayList.add(r);
                }
            }
        }
        return displayList;
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
        if (currentSelection.equals(mItem)) {
            onSaveItem();
            return;
        }
        int oldSelectedPosition = mSelector.getSelectedPosition();
        currentSelection = mItem;
        int position = holder.getAdapterPosition();
        mSelector.setSelected(position, currentSelection.getId(), true);
        mAdapter.notifyItemChanged(oldSelectedPosition);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        StandardRound item = (StandardRound) holder.getItem();
        if (item.club == StandardRoundFactory.CUSTOM) {
            Intent i = new Intent(getActivity(),
                    SimpleFragmentActivity.EditStandardRoundActivity.class);
            i.putExtra(ITEM, Parcels.wrap(item));
            startActivityForResult(i, NEW_STANDARD_ROUND);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.use_as_template)
                    .setMessage(R.string.create_copy)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        Intent i = new Intent(getActivity(),
                                SimpleFragmentActivity.EditStandardRoundActivity.class);
                        i.putExtra(ITEM, Parcels.wrap(item));
                        startActivityForResult(i, NEW_STANDARD_ROUND);
                        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
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
