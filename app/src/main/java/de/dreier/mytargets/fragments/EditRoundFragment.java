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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.RoundTemplateDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.views.NumberPicker;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.TargetSelector;

public class EditRoundFragment extends EditFragmentBase {
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

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTraining = arguments.getLong(TRAINING_ID, -1);
            mRound = arguments.getLong(ROUND_ID, -1);
        }
        setUpToolbar(rootView);

        View not_editable = rootView.findViewById(R.id.not_editable);
        View distance_layout = rootView.findViewById(R.id.distance_layout);
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

        Button remove = (Button) rootView.findViewById(R.id.remove_button);

        if (mRound == -1) {
            setTitle(R.string.new_round);
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
            remove.setVisibility(View.GONE);
        } else {
            setTitle(R.string.edit_round);
            RoundDataSource roundDataSource = new RoundDataSource(getContext());
            Round round = roundDataSource.get(mRound);
            distanceSpinner.setItem(round.info.distance);
            comment.setText(round.comment);
            not_editable.setVisibility(View.GONE);
            StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource(getContext());
            StandardRound standardRound = standardRoundDataSource.get(round.info.standardRound);
            if (standardRound.club != StandardRound.CUSTOM_PRACTICE) {
                distance_layout.setVisibility(View.GONE);
            } else if (standardRound.getRounds().size() > 1) {
                remove.setOnClickListener(v -> {
                    roundDataSource.delete(round);
                    getActivity().finish();
                });
            } else {
                remove.setVisibility(View.GONE);
            }
        }
        return rootView;
    }

    @Override
    protected void onSave() {
        getActivity().finish();
        if (mRound == -1) {
            onSaveRound();
            Intent i = new Intent(getActivity(), InputActivity.class);
            i.putExtra(InputActivity.ROUND_ID, mRound);
            i.putExtra(InputActivity.PASSE_IND, 0);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            onSaveRound();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    private void onSaveRound() {
        RoundDataSource roundDataSource = new RoundDataSource(getContext());
        Training training = new TrainingDataSource(getContext()).get(mTraining);
        StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource(getContext());
        StandardRound standardRound = standardRoundDataSource.get(training.standardRoundId);

        SharedPreferences.Editor editor = prefs.edit();
        Round round;
        if (mRound != -1) {
            round = roundDataSource.get(mRound);
        } else {
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

        if (standardRound.club == StandardRound.CUSTOM_PRACTICE) {
            round.info.distance = distanceSpinner.getSelectedItem();
            new RoundTemplateDataSource(getContext()).update(round.info);
        }
        roundDataSource.update(round);

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