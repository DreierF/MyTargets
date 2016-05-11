package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
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
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.TargetSelector;

public abstract class EditRoundPropertiesFragmentBase extends EditFragmentBase {
    public static final String TRAINING_ID = "training_id";

    protected long trainingId = -1;

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
            trainingId = arguments.getLong(TRAINING_ID, -1);
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

    protected void updateArrowsLabel() {
        arrowsLabel.setText(getResources().getQuantityString(R.plurals.arrow, arrows.getProgress(), arrows.getProgress()));
    }

    protected void loadRoundDefaultValues() {
        int distance = prefs.getInt("distance", 10);
        String unit = prefs.getString("unit", "m");
        distanceSpinner.setItem(new Distance(distance, unit));
        arrows.setProgress(prefs.getInt("ppp", 3));
        targetSpinner.setItem(new Target(prefs.getInt("target", 0),
                prefs.getInt("scoring_style", 0),
                new Diameter(prefs.getInt("size_target", 60),
                        prefs.getString("unit_target", Diameter.CENTIMETER))));
    }

    @NonNull
    protected RoundTemplate getRoundTemplate() {
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.target = targetSpinner.getSelectedItem();
        roundTemplate.targetTemplate = roundTemplate.target;
        roundTemplate.arrowsPerPasse = arrows.getProgress();
        roundTemplate.passes = 1;
        roundTemplate.distance = distanceSpinner.getSelectedItem();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("ppp", roundTemplate.arrowsPerPasse);
        editor.putInt("rounds", roundTemplate.passes);
        editor.putInt("distance", roundTemplate.distance.value);
        editor.putString("unit", roundTemplate.distance.unit);
        editor.putInt("target", (int) roundTemplate.target.getId());
        editor.putInt("scoring_style", roundTemplate.target.scoringStyle);
        editor.putInt("size_target", roundTemplate.target.size.value);
        editor.putString("unit_target", roundTemplate.target.size.unit);
        editor.apply();

        return roundTemplate;
    }

    protected void openPasseForNewRound(long trainingId, long roundId) {
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
