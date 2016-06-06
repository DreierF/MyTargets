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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.views.DynamicItemLayout;
import de.dreier.mytargets.views.NumberPicker;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.TargetSelector;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class EditStandardRoundFragment extends EditFragmentBase
        implements DynamicItemLayout.OnBindListener<RoundTemplate> {

    private RadioButton indoor;
    private DynamicItemLayout<RoundTemplate> rounds;
    private EditText name;
    private long standardRoundId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_edit_standard_round, container, false);

        setUpToolbar(rootView);

        StandardRound standardRound = null;
        if (getArguments() != null) {
            standardRound = Parcels.unwrap(getArguments().getParcelable(ITEM));
        }

        // Indoor / outdoor
        RadioButton outdoor = (RadioButton) rootView.findViewById(R.id.outdoor);
        indoor = (RadioButton) rootView.findViewById(R.id.indoor);

        // Name
        name = (EditText) rootView.findViewById(R.id.name);

        // Rounds
        //noinspection unchecked
        rounds = (DynamicItemLayout<RoundTemplate>) rootView.findViewById(R.id.rounds);
        rounds.setLayoutResource(R.layout.dynamicitem_round, RoundTemplate.class);
        rounds.setOnBindListener(this);
        rounds.rebindOnIndexChanged(true);

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
            rounds.addItem(round);
        } else {
            setTitle(R.string.edit_standard_round);
            // Load saved values
            indoor.setChecked(standardRound.indoor);
            outdoor.setChecked(!standardRound.indoor);
            ArrayList<RoundTemplate> rounds = standardRound.getRounds();
            if (standardRound.club == StandardRoundFactory.CUSTOM) {
                name.setText(standardRound.name);
                standardRoundId = standardRound.getId();
            } else {
                name.setText(String.format("%s %s", getString(R.string.custom), standardRound.name));
                for(RoundTemplate round : rounds) {
                    round.setId(-1);
                }
            }
            this.rounds.setList(rounds);
        }
        return rootView;
    }

    @Override
    protected void onSave() {
        StandardRound standardRound = new StandardRound();
        standardRound.setId(standardRoundId);
        standardRound.club = StandardRoundFactory.CUSTOM;
        standardRound.name = name.getText().toString();
        standardRound.indoor = indoor.isChecked();
        standardRound.setRounds(rounds.getList());
        new StandardRoundDataSource(getContext()).update(standardRound);

        SettingsManager.setIndoor(standardRound.indoor);

        RoundTemplate round = rounds.getList().get(0);
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
    public void onBind(View view, final RoundTemplate round, int index) {
        // Initialize empty round with default values
        if (round.distance == null) {
            RoundTemplate r = rounds.getList().get(index - 1);
            round.passes = r.passes;
            round.arrowsPerPasse = r.arrowsPerPasse;
            round.distance = r.distance;
            round.target = r.target;
            round.target.size = r.target.size;
            round.targetTemplate = r.targetTemplate;
        }

        // Set title of round
        TextView title = ((TextView) view.findViewById(R.id.round_number));
        title.setText(getResources().getQuantityString(R.plurals.rounds, index + 1, index + 1));
        round.index = index;

        final DistanceSelector distanceSpinner = (DistanceSelector) view
                .findViewById(R.id.distanceSpinner);
        distanceSpinner.setOnActivityResultContext(this);
        distanceSpinner.setItemIndex(index);
        distanceSpinner.setItem(round.distance);
        distanceSpinner.setOnUpdateListener(item -> round.distance = item);

        // Target round
        final TargetSelector targetSpinner = (TargetSelector) view
                .findViewById(R.id.targetSpinner);
        targetSpinner.setOnActivityResultContext(this);
        targetSpinner.setItemIndex(index);
        targetSpinner.setOnUpdateListener(item -> {
            round.target = item;
            round.targetTemplate = item;
        });
        targetSpinner.setItem(round.target);

        // Passes
        NumberPicker passes = (NumberPicker) view.findViewById(R.id.passes);
        passes.setTextPattern(R.plurals.passe);
        passes.setOnValueChangedListener(val -> round.passes = val);
        passes.setValue(round.passes);

        // Arrows per passe
        NumberPicker arrows = (NumberPicker) view.findViewById(R.id.arrows);
        arrows.setTextPattern(R.plurals.arrow);
        arrows.setMinimum(1);
        arrows.setMaximum(12);
        arrows.setOnValueChangedListener(val -> round.arrowsPerPasse = val);
        arrows.setValue(round.arrowsPerPasse);

        ImageButton remove = (ImageButton) view.findViewById(R.id.remove);
        if (index == 0) {
            remove.setVisibility(View.GONE);
        } else {
            remove.setOnClickListener(view1 -> rounds.remove(round, R.string.round_removed));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (int i = 0; i < rounds.getChildCount() - 1; i++) {
            final View round = rounds.getChildAt(i);
            final DistanceSelector distanceSpinner = (DistanceSelector) round
                    .findViewById(R.id.distanceSpinner);
            distanceSpinner.onActivityResult(requestCode, resultCode, data);
            final TargetSelector targetSpinner = (TargetSelector) round
                    .findViewById(R.id.targetSpinner);
            targetSpinner.onActivityResult(requestCode, resultCode, data);
        }
    }
}