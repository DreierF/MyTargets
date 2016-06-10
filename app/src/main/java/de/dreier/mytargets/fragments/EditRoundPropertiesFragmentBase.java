package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.TargetSelector;

public abstract class EditRoundPropertiesFragmentBase extends EditFragmentBase {

    long trainingId = -1;

    @Bind(R.id.distanceSpinner)
    DistanceSelector distanceSpinner;

    @Bind(R.id.targetSpinner)
    TargetSelector targetSpinner;

    @Bind(R.id.arrows)
    DiscreteSeekBar arrows;

    @Bind(R.id.arrowsLabel)
    TextView arrowsLabel;

    @Bind(R.id.notEditable)
    View notEditable;

    protected abstract int getLayoutResource();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResource(), container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            trainingId = arguments.getLong(FragmentBase.ITEM_ID, -1);
        }

        setUpToolbar(rootView);

        arrows.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                updateArrowsLabel();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
        targetSpinner.setOnActivityResultContext(this);
        distanceSpinner.setOnActivityResultContext(this);
        return rootView;
    }

    void updateArrowsLabel() {
        arrowsLabel.setText(getResources()
                .getQuantityString(R.plurals.arrow, arrows.getProgress(), arrows.getProgress()));
    }

    void loadRoundDefaultValues() {
        distanceSpinner.setItem(SettingsManager.getDistance());
        arrows.setProgress(SettingsManager.getArrowsPerPasse());
        targetSpinner.setItem(SettingsManager.getTarget());
    }

    @NonNull
    RoundTemplate getRoundTemplate() {
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.target = targetSpinner.getSelectedItem();
        roundTemplate.targetTemplate = roundTemplate.target;
        roundTemplate.arrowsPerPasse = arrows.getProgress();
        roundTemplate.passes = 1;
        roundTemplate.distance = distanceSpinner.getSelectedItem();

        SettingsManager.setTarget(roundTemplate.target);
        SettingsManager.setDistance(roundTemplate.distance);
        SettingsManager.setArrowsPerPasse(roundTemplate.arrowsPerPasse);
        return roundTemplate;
    }

    void openPasseForNewRound(long trainingId, long roundId) {
        if (trainingId != -1) {
            Intent i = new Intent(getActivity(), SimpleFragmentActivity.TrainingActivity.class);
            i.putExtra(TrainingFragment.ITEM_ID, trainingId);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
        }

        Intent i = new Intent(getActivity(), InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, roundId);
        i.putExtra(InputActivity.PASSE_IND, 0);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        targetSpinner.onActivityResult(requestCode, resultCode, data);
        distanceSpinner.onActivityResult(requestCode, resultCode, data);
    }
}
