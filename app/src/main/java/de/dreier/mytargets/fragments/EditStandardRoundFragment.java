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
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.views.DistanceDialogSpinner;
import de.dreier.mytargets.views.DynamicItemLayout;
import de.dreier.mytargets.views.NumberPicker;
import de.dreier.mytargets.views.TargetDialogSpinner;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class EditStandardRoundFragment extends Fragment
        implements DynamicItemLayout.OnBindListener<RoundTemplate> {

    private RadioButton indoor;
    private DynamicItemLayout<RoundTemplate> rounds;
    private EditText name;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_edit_standard_round, container, false);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        setHasOptionsMenu(true);

        Bundle i = getArguments();
        StandardRound standardRound = null;
        if (i != null) {
            standardRound = (StandardRound) i.getSerializable(ITEM);
        }
        SharedPreferences prefs = activity.getSharedPreferences(MyBackupAgent.PREFS, 0);

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
            name.setText(R.string.custom_round);
            // Initialise with default values
            indoor.setChecked(prefs.getBoolean("indoor", false));
            outdoor.setChecked(!prefs.getBoolean("indoor", false));

            RoundTemplate round = new RoundTemplate();
            round.arrowsPerPasse = prefs.getInt("ppp", 3);
            round.passes = prefs.getInt("rounds", 10);
            int tid = prefs.getInt("target", 0);
            int scoring = prefs.getInt("scoring", 0);
            round.target = TargetFactory.createTarget(getActivity(), tid, scoring);
            round.target.size = round.target.getDiameters()[0];
            round.targetTemplate = round.target;
            long distId = prefs.getLong("distanceId", new Distance(18, "m").getId());
            round.distance = Distance.fromId(distId);
            rounds.addItem(round);
        } else {
            getActivity().setTitle(R.string.edit_standard_round);
            // Load saved values
            indoor.setChecked(standardRound.indoor);
            outdoor.setChecked(!standardRound.indoor);
            if (standardRound.getId() == -1) {
                name.setText(standardRound.name);
            } else {
                name.setText(getActivity().getString(R.string.custom) + " " + standardRound.name);
            }
            rounds.setList(standardRound.getRounds());
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSave() {
        StandardRound standardRound = new StandardRound();
        standardRound.setId(-1);
        standardRound.club = StandardRound.CUSTOM;
        standardRound.name = name.getText().toString();
        standardRound.indoor = indoor.isChecked();
        standardRound.setRounds(rounds.getList());

        SharedPreferences prefs = getActivity().getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("indoor", standardRound.indoor);

        RoundTemplate round = rounds.getList().get(0);
        editor.putInt("ppp", round.arrowsPerPasse);
        editor.putInt("rounds", round.passes);
        editor.putInt("target", (int) round.target.getId());
        editor.putInt("scoring", round.target.scoringStyle);
        editor.putLong("distanceId", round.distance.getId());

        editor.apply();

        Intent data = new Intent();
        data.putExtra(ITEM, standardRound);
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

        // Distance
        final DistanceDialogSpinner distanceSpinner = (DistanceDialogSpinner) view
                .findViewById(R.id.distance_spinner);
        distanceSpinner.setItem(round.distance);
        distanceSpinner.setOnUpdateListener(item -> round.distance = item);

        // Target round
        final TargetDialogSpinner targetSpinner = (TargetDialogSpinner) view
                .findViewById(R.id.target_spinner);
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
        NumberPicker arrows = (NumberPicker) view.findViewById(R.id.ppp);
        arrows.setTextPattern(R.plurals.arrow);
        arrows.setMinimum(1);
        arrows.setMaximum(10);
        arrows.setOnValueChangedListener(val -> round.arrowsPerPasse = val);
        arrows.setValue(round.arrowsPerPasse);

        ImageButton remove = (ImageButton) view.findViewById(R.id.remove);
        if (index == 0) {
            remove.setVisibility(View.GONE);
        } else {
            remove.setOnClickListener(view1 -> rounds.remove(round, R.string.round_removed));
        }
    }
}