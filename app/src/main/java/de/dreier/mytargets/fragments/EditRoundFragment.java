/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.RoundTemplateDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;

public class EditRoundFragment extends EditRoundPropertiesFragmentBase {
    public static final String ROUND_ID = "round_id";

    private long roundId = -1;

    @Bind(R.id.arrows)
    DiscreteSeekBar arrows;

    @Bind(R.id.comment)
    EditText comment;

    @Bind(R.id.distanceLayout)
    View distanceLayout;

    @Bind(R.id.remove_button)
    Button remove;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_edit_round;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            roundId = arguments.getLong(ROUND_ID, -1);
        }

        if (roundId == -1) {
            setTitle(R.string.new_round);
            loadRoundDefaultValues();
            comment.setText("");
            remove.setVisibility(View.GONE);
        } else {
            setTitle(R.string.edit_round);
            RoundDataSource roundDataSource = new RoundDataSource(getContext());
            Round round = roundDataSource.get(roundId);
            distanceSpinner.setItem(round.info.distance);
            comment.setText(round.comment);
            notEditable.setVisibility(View.GONE);
            StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource(getContext());
            StandardRound standardRound = standardRoundDataSource.get(round.info.standardRound);
            if (standardRound.club != StandardRoundFactory.CUSTOM_PRACTICE) {
                distanceLayout.setVisibility(View.GONE);
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
    public void onDestroyView() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onSave() {
        getActivity().finish();
        if (roundId == -1) {
            Round round = onSaveRound();
            openPasseForNewRound(-1, round.getId());
        } else {
            onSaveRound();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    private Round onSaveRound() {
        RoundDataSource roundDataSource = new RoundDataSource(getContext());
        Training training = new TrainingDataSource(getContext()).get(trainingId);
        StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource(getContext());
        StandardRound standardRound = standardRoundDataSource.get(training.standardRoundId);

        Round round;
        if (roundId != -1) {
            round = roundDataSource.get(roundId);
        } else {
            round = new Round();
            round.training = trainingId;
            round.info = getRoundTemplate();
            round.info.standardRound = standardRound.getId();
        }

        round.comment = comment.getText().toString();

        if (standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE) {
            round.info.distance = distanceSpinner.getSelectedItem();
            round.info.index = standardRound.getRounds().size();
            new RoundTemplateDataSource(getContext()).update(round.info);
        }
        roundDataSource.update(round);

        return round;
    }
}