/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.views.NumberPicker;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.TargetSelector;

public class EditRoundFragment extends Fragment {
    public static final String TRAINING_ID = "training_id";
    public static final String ROUND_ID = "round_id";

    private long mTraining = -1, mRound = -1;

    private DistanceSelector distanceSpinner;
    private TargetSelector targetSpinner;
    private NumberPicker passes, arrows;
    private EditText comment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_round, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTraining = arguments.getLong(TRAINING_ID, -1);
            mRound = arguments.getLong(ROUND_ID, -1);
        }
        SharedPreferences prefs = activity.getSharedPreferences(MyBackupAgent.PREFS, 0);

        View not_editable = rootView.findViewById(R.id.not_editable);
        distanceSpinner = (DistanceSelector) rootView.findViewById(R.id.distance_spinner);
        targetSpinner = (TargetSelector) rootView.findViewById(R.id.target_spinner);

        // Passes
        passes = (NumberPicker) rootView.findViewById(R.id.passes);
        passes.setTextPattern(R.plurals.passe);

        // Arrows per passe
        arrows = (NumberPicker) rootView.findViewById(R.id.ppp);
        arrows.setTextPattern(R.plurals.arrow);
        arrows.setMinimum(1);
        arrows.setMaximum(10);

        // Comment
        comment = (EditText) rootView.findViewById(R.id.comment);

        if (mRound == -1) {
            activity.getSupportActionBar().setTitle(R.string.new_round);
            int distance = prefs.getInt("distance", 10);
            String unit = prefs.getString("unit", "m");
            distanceSpinner.setItem(new Distance(distance, unit));
            arrows.setValue(prefs.getInt("ppp", 3));
            passes.setValue(prefs.getInt("rounds", 10));
            Target target = TargetFactory.createTarget(activity, prefs.getInt("target", 0),
                    prefs.getInt("scoring_style", 0));
            target.size = new Diameter(prefs.getInt("size_target", 60),
                    prefs.getString("unit_target", Diameter.CENTIMETER));
            targetSpinner.setItem(target);
            comment.setText("");
        } else {
            activity.getSupportActionBar().setTitle(R.string.edit_round);
            DatabaseManager db = DatabaseManager.getInstance(activity);
            Round round = db.getRound(mRound);
            distanceSpinner.setItem(round.info.distance);
            comment.setText(round.comment);
            not_editable.setVisibility(View.GONE);
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
            getActivity().finish();
            if (mRound == -1) {
                onSaveTraining();
                Intent i = new Intent(getActivity(), InputActivity.class);
                i.putExtra(InputActivity.ROUND_ID, mRound);
                i.putExtra(InputActivity.PASSE_IND, 0);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            } else {
                onSaveTraining();
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSaveTraining() {
        DatabaseManager db = DatabaseManager.getInstance(getContext());
        SharedPreferences prefs = getActivity().getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        Round round;
        if (mRound != -1) {
            round = db.getRound(mRound);
        } else {
            Training training = db.getTraining(mTraining);
            StandardRound standardRound = db.getStandardRound(training.standardRoundId);
            round = new Round();
            round.training = mTraining;
            round.info = new RoundTemplate();
            round.info.standardRound = standardRound.getId();
            round.info.index = standardRound.getRounds().size();
            round.info.target = targetSpinner.getSelectedItem();
            round.info.targetTemplate = round.info.target;
            round.info.arrowsPerPasse = arrows.getValue();
            round.info.passes = passes.getValue();
        }

        round.comment = comment.getText().toString();
        round.info.distance = distanceSpinner.getSelectedItem();
        db.update(round.info);
        db.update(round);

        mRound = round.getId();

        editor.putInt("ppp", round.info.arrowsPerPasse);
        editor.putInt("rounds", round.info.passes);
        editor.putInt("distance", round.info.distance.value);
        editor.putString("unit", round.info.distance.unit);
        editor.putInt("target", (int) round.info.target.getId());
        editor.putInt("scoring_style", round.info.target.scoringStyle);
        editor.putInt("size_target", round.info.target.size.value);
        editor.putString("unit_target", round.info.target.size.unit);
        editor.apply();
    }
}