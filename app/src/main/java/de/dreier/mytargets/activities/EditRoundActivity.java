package de.dreier.mytargets.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.iangclifton.android.floatlabel.FloatLabel;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.ArrowItemAdapter;
import de.dreier.mytargets.adapters.BowItemAdapter;
import de.dreier.mytargets.adapters.TargetItemAdapter;
import de.dreier.mytargets.fragments.PasseFragment;
import de.dreier.mytargets.fragments.RoundFragment;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.views.DialogSpinner;
import de.dreier.mytargets.views.NumberPicker;

public class EditRoundActivity extends ActionBarActivity {

    public static final String TRAINING_ID = "training_id";
    public static final String ROUND_ID = "round_id";
    public static final String FROM_PASSE = "from_passe";
    private long mTraining = -1, mRound = -1;

    private Spinner distance;
    private RadioButton indoor;
    private DialogSpinner bow;
    private DialogSpinner arrow;
    private DialogSpinner target;
    public static final int[] distanceValues = {10, 15, 18, 20, 25, 30, 40, 50, 60, 70, 90};
    private boolean mCalledFromPasse = false;
    private int mBowId = 0;
    private EditText training;
    private FloatLabel comment;
    private View customDist;
    private EditText distanceVal;
    private boolean custom = false;
    private NumberPicker ppp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_round);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Intent i = getIntent();
        if (i != null) {
            if (i.hasExtra(TRAINING_ID)) {
                mTraining = i.getLongExtra(TRAINING_ID, -1);
            }
            if (i.hasExtra(ROUND_ID)) {
                mRound = i.getLongExtra(ROUND_ID, -1);
            }
            mCalledFromPasse = i.hasExtra(FROM_PASSE);
        }
        SharedPreferences prefs = getSharedPreferences(MyBackupAgent.PREFS, 0);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(
                        R.array.distances));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        training = (EditText) findViewById(R.id.training);
        customDist = findViewById(R.id.customDist);
        distanceVal = (EditText) findViewById(R.id.distanceVal);
        distance = (Spinner) findViewById(R.id.distance);
        distance.setAdapter(adapter);
        distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == distanceValues.length) {
                    distance.setVisibility(View.GONE);
                    customDist.setVisibility(View.VISIBLE);
                    custom = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Indoor / outdoor
        RadioButton outdoor = (RadioButton) findViewById(R.id.outdoor);
        indoor = (RadioButton) findViewById(R.id.indoor);

        // Points per passe
        ppp = (NumberPicker) findViewById(R.id.ppp);
        ppp.setMinimum(1);
        ppp.setMaximum(10);

        // Bow
        bow = (DialogSpinner) findViewById(R.id.bow);
        bow.setAdapter(new BowItemAdapter(this), R.string.bow);
        Button addBow = (Button) findViewById(R.id.add_bow);
        i = new Intent(this, EditBowActivity.class);
        bow.setAddButton(addBow, R.string.add_bow, i);

        // Arrow
        arrow = (DialogSpinner) findViewById(R.id.arrow);
        arrow.setAdapter(new ArrowItemAdapter(this), R.string.arrow);
        Button addArrow = (Button) findViewById(R.id.add_arrow);
        i = new Intent(this, EditArrowActivity.class);
        arrow.setAddButton(addArrow, R.string.add_arrow, i);

        // Target round
        target = (DialogSpinner) findViewById(R.id.target_spinner);
        target.setAdapter(new TargetItemAdapter(this), R.string.target_round);

        // Comment
        comment = (FloatLabel) findViewById(R.id.comment);

        if (mRound == -1) {
            // Initialise with default values
            int distVal = prefs.getInt("distance", 10);
            int distanceInd = -1;
            for (int j = 0; j < EditRoundActivity.distanceValues.length; j++) {
                if (EditRoundActivity.distanceValues[j] == distVal) {
                    distanceInd = j;
                }
            }
            if (distanceInd == -1) {
                distance.setVisibility(View.GONE);
                customDist.setVisibility(View.VISIBLE);
                distanceVal.setText("" + distVal);
                custom = true;
            } else {
                distance.setSelection(distanceInd);
                distance.setVisibility(View.VISIBLE);
                customDist.setVisibility(View.GONE);
                custom = false;
            }
            indoor.setChecked(prefs.getBoolean("indoor", false));
            outdoor.setChecked(!prefs.getBoolean("indoor", false));
            ppp.setValue(prefs.getInt("ppp", 3));
            bow.setItemId(prefs.getInt("bow", 0));
            arrow.setItemId(prefs.getInt("arrow", 0));
            target.setItemId(prefs.getInt("target", 2));
            comment.setText("");
        } else {
            // Load saved values
            DatabaseManager db = DatabaseManager.getInstance(this);
            Round r = db.getRound(mRound);
            if (r.distanceInd == -1) {
                distance.setVisibility(View.GONE);
                customDist.setVisibility(View.VISIBLE);
                distanceVal.setText("" + r.distanceVal);
                custom = true;
            } else {
                distance.setSelection(r.distanceInd);
                distance.setVisibility(View.VISIBLE);
                customDist.setVisibility(View.GONE);
                custom = false;
            }
            indoor.setChecked(r.indoor);
            outdoor.setChecked(!r.indoor);
            ppp.setValue(r.ppp);
            bow.setItemId(r.bow);
            arrow.setItemId(r.arrow);
            target.setItemId(r.target);
            comment.setText(r.comment);

            ppp.setEnabled(false);
            bow.setEnabled(false);
            arrow.setEnabled(false);
            target.setEnabled(false);
            ((TextView) findViewById(R.id.label_ppp)).setTextColor(0xff444444);
            ((TextView) findViewById(R.id.label_bow)).setTextColor(0xff444444);
            ((TextView) findViewById(R.id.label_arrow)).setTextColor(0xff444444);
            ((TextView) findViewById(R.id.label_target)).setTextColor(0xff444444);
        }
        if (mTraining == -1) {
            training.setText(getString(R.string.training));
            getSupportActionBar().setTitle(R.string.new_training);
        } else {
            View training_container = findViewById(R.id.training_container);
            training_container.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bow.setAdapter(new BowItemAdapter(this), R.string.bow);
        arrow.setAdapter(new ArrowItemAdapter(this), R.string.arrow);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSave() {
        Round round = new Round();
        round.target = (int) target.getSelectedItemId();

        if (bow.getAdapter().getCount() == 0 && mBowId == 0 && round.target == 3) {
            new AlertDialog.Builder(this).setTitle(R.string.title_compound)
                    .setMessage(R.string.msg_compound_type)
                    .setPositiveButton(R.string.compound_bow,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mBowId = -2;
                                    onSave();
                                }
                            })
                    .setNegativeButton(R.string.other_bow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBowId = -1;
                            onSave();
                        }
                    })
                    .show();
            return;
        }

        DatabaseManager db = DatabaseManager.getInstance(this);

        String title = training.getText().toString();
        if (mTraining == -1) {
            mTraining = db.newTraining(title);
        }

        round.id = mRound;
        round.training = mTraining;
        round.bow = bow.getSelectedItemId();
        round.arrow = arrow.getSelectedItemId();
        if (round.bow == 0) {
            round.bow = mBowId;
        }

        if (custom) {
            round.distanceVal = Integer.parseInt(distanceVal.getText().toString());
        } else {
            round.distanceVal = distanceValues[distance.getSelectedItemPosition()];
        }
        round.unit = "m";

        round.ppp = ppp.getValue();
        round.indoor = indoor.isChecked();
        round.comment = comment.getTextString();
        db.updateRound(round);

        SharedPreferences prefs = getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("bow", (int) bow.getSelectedItemId());
        editor.putInt("arrow", (int) arrow.getSelectedItemId());
        editor.putInt("distance", round.distanceVal);
        editor.putInt("ppp", round.ppp);
        editor.putInt("target", round.target);
        editor.putBoolean("indoor", round.indoor);
        editor.apply();

        finish();
        if (mRound == -1) {
            Intent i = new Intent(this, SimpleFragmentActivity.RoundActivity.class);
            i.putExtra(RoundFragment.TRAINING_ID, mTraining);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

            i = new Intent(this, SimpleFragmentActivity.PasseActivity.class);
            i.putExtra(PasseFragment.TRAINING_ID, mTraining);
            i.putExtra(PasseFragment.ROUND_ID, round.id);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

            i = new Intent(this, InputActivity.class);
            i.putExtra(InputActivity.ROUND_ID, round.id);
            startActivity(i);

            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        if (mCalledFromPasse) {
            overridePendingTransition(R.anim.right_in_half, R.anim.left_out_complete);
        } else {
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }
}
