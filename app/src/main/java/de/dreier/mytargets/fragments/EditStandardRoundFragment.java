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
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.adapters.DynamicItemHolder;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.views.NumberPicker;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.SelectorBase;
import de.dreier.mytargets.views.selector.TargetSelector;
import icepick.Icepick;
import icepick.State;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class EditStandardRoundFragment extends EditFragmentBase {

    @Bind(R.id.indoor)
    RadioButton indoor;
    @Bind(R.id.outdoor)
    RadioButton outdoor;

    @Bind(R.id.rounds)
    RecyclerView rounds;

    @Bind(R.id.name)
    EditText name;
    @State(ParcelsBundler.class)
    ArrayList<RoundTemplate> roundTemplateList = new ArrayList<>();
    private long standardRoundId = -1;
    private RoundTemplateAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_edit_standard_round, container, false);

        ButterKnife.bind(this, rootView);
        setUpToolbar((Toolbar) rootView.findViewById(R.id.toolbar));
        Icepick.restoreInstanceState(this, savedInstanceState);

        StandardRound standardRound = null;
        if (getArguments() != null) {
            standardRound = Parcels.unwrap(getArguments().getParcelable(ITEM));
        }

        if (savedInstanceState == null) {
            if (standardRound == null) {
                setTitle(R.string.new_round_template);
                name.setText(R.string.custom_round);
                // Initialise with default values
                indoor.setChecked(SettingsManager.getIndoor());
                outdoor.setChecked(!SettingsManager.getIndoor());

                RoundTemplate round = new RoundTemplate();
                round.arrowsPerPasse = SettingsManager.getArrowsPerPasse();
                round.passes = SettingsManager.getPasses();
                round.target = SettingsManager.getTarget();
                round.targetTemplate = round.target;
                round.distance = SettingsManager.getDistance();
                name.setText(standardRound.name);
                roundTemplateList.add(round);
            } else {
                setTitle(R.string.edit_standard_round);
                // Load saved values
                indoor.setChecked(standardRound.indoor);
                outdoor.setChecked(!standardRound.indoor);
                roundTemplateList = standardRound.rounds;
                if (standardRound.club == StandardRoundFactory.CUSTOM) {
                    name.setText(standardRound.name);
                    standardRoundId = standardRound.getId();
                } else {
                    name.setText(
                            String.format("%s %s", getString(R.string.custom), standardRound.name));
                    for (RoundTemplate round : roundTemplateList) {
                        round.setId(-1);
                    }
                }
            }
        }

        adapter = new RoundTemplateAdapter(this, roundTemplateList);
        rounds.setAdapter(adapter);

        return rootView;
    }

    @OnClick(R.id.addButton)
    public void onAddSightSetting() {
        RoundTemplate r = roundTemplateList.get(roundTemplateList.size() - 1);
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.passes = r.passes;
        roundTemplate.arrowsPerPasse = r.arrowsPerPasse;
        roundTemplate.distance = r.distance;
        roundTemplate.target = r.target;
        roundTemplate.target.size = r.target.size;
        roundTemplate.targetTemplate = r.targetTemplate;
        roundTemplateList.add(roundTemplate);
        adapter.notifyItemInserted(roundTemplateList.size() - 1);
    }

    @OnClick(R.id.deleteStandardRound)
    public void onDeleteStandardRound() {
        new StandardRoundDataSource().delete(standardRoundId);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    protected void onSave() {
        StandardRound standardRound = new StandardRound();
        standardRound.setId(standardRoundId);
        standardRound.club = StandardRoundFactory.CUSTOM;
        standardRound.name = name.getText().toString();
        standardRound.indoor = indoor.isChecked();
        standardRound.rounds = roundTemplateList;
        new StandardRoundDataSource().update(standardRound);

        SettingsManager.setIndoor(standardRound.indoor);

        RoundTemplate round = roundTemplateList.get(0);
        SettingsManager.setArrowsPerPasse(round.arrowsPerPasse);
        SettingsManager.setPasses(round.passes);
        SettingsManager.setTarget(round.target);
        SettingsManager.setDistance(round.distance);

        Intent data = new Intent();
        data.putExtra(ITEM, Parcels.wrap(standardRound));
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            final int index = intentData.getInt(SelectorBase.INDEX);
            final Parcelable parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
            switch (requestCode) {
                case DistanceSelector.DISTANCE_REQUEST_CODE:
                    roundTemplateList.get(index).distance = Parcels.unwrap(parcelable);
                    adapter.notifyItemChanged(index);
                    break;
                case TargetSelector.TARGET_REQUEST_CODE:
                    roundTemplateList.get(index).target = Parcels.unwrap(parcelable);
                    roundTemplateList.get(index).targetTemplate = Parcels.unwrap(parcelable);
                    adapter.notifyItemChanged(index);
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    public static class RoundTemplateHolder extends DynamicItemHolder<RoundTemplate> {
        @Bind(R.id.arrows)
        NumberPicker arrows;
        @Bind(R.id.passes)
        NumberPicker passes;
        @Bind(R.id.targetSpinner)
        TargetSelector targetSpinner;
        @Bind(R.id.round_number)
        TextView title;
        @Bind(R.id.distanceSpinner)
        DistanceSelector distanceSpinner;
        @Bind(R.id.remove)
        ImageButton remove;

        public RoundTemplateHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onBind(RoundTemplate roundTemplate, int position, Fragment fragment, View.OnClickListener removeListener) {
            item = roundTemplate;

            // Set title of round
            title.setText(fragment.getContext().getResources()
                    .getQuantityString(R.plurals.rounds, position + 1, position + 1));
            item.index = position;

            distanceSpinner.setOnActivityResultContext(fragment);
            distanceSpinner.setItemIndex(position);
            distanceSpinner.setItem(item.distance);

            // Target round
            targetSpinner.setOnActivityResultContext(fragment);
            targetSpinner.setItemIndex(position);
            targetSpinner.setItem(item.target);

            // Passes
            passes.setTextPattern(R.plurals.passe);
            passes.setOnValueChangedListener(val -> item.passes = val);
            passes.setValue(item.passes);

            // Arrows per passe
            arrows.setTextPattern(R.plurals.arrow);
            arrows.setMinimum(1);
            arrows.setMaximum(12);
            arrows.setOnValueChangedListener(val -> item.arrowsPerPasse = val);
            arrows.setValue(item.arrowsPerPasse);

            if (position == 0) {
                remove.setVisibility(View.GONE);
            } else {
                remove.setVisibility(View.VISIBLE);
                remove.setOnClickListener(removeListener);
            }
        }
    }

    private class RoundTemplateAdapter extends DynamicItemAdapter<RoundTemplate> {
        public RoundTemplateAdapter(Fragment fragment, List<RoundTemplate> list) {
            super(fragment, list, R.string.round_removed);
        }

        @Override
        public DynamicItemHolder<RoundTemplate> onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.dynamicitem_round_template, parent, false);
            return new RoundTemplateHolder(v);
        }
    }
}
