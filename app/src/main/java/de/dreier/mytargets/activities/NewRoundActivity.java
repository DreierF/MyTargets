package de.dreier.mytargets.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.iangclifton.android.floatlabel.FloatLabel;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.TargetItemAdapter;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.adapters.BowItemAdapter;
import de.dreier.mytargets.utils.MyBackupAgent;

public class NewRoundActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String TRAINING_ID = "training_id";
    public static final String ROUND_ID = "round_id";
    public static final String FROM_PASSE = "from_passe";
    private long mTraining = -1, mRound = -1;

    private Spinner distance;
    private RadioButton indoor;
    private Spinner bow, target;
    public static final String[] distances = {"10m", "15m", "18m", "20m", "25m", "30m", "40m", "50m", "60m", "70m", "90m", "Benutzerdefiniert"};
    public static final int[] distanceValues = {10, 15, 18, 20, 25, 30, 40, 50, 60, 70, 90};
    private RadioButton ppp2, ppp3;
    private Button addBow;
    private boolean mCalledFromPasse = false;
    private int mBowType = -1;
    private FloatLabel training;
    private View customDist;
    private EditText distanceVal;
    private boolean custom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_round);

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


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, distances);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        training = (FloatLabel) findViewById(R.id.training);
        customDist = findViewById(R.id.customDist);
        distanceVal = (EditText)findViewById(R.id.distanceVal);
        distance = (Spinner) findViewById(R.id.distance);
        distance.setAdapter(adapter);
        distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==distances.length-1) {
                    distance.setVisibility(View.GONE);
                    customDist.setVisibility(View.VISIBLE);
                    custom = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        RadioButton outdoor = (RadioButton) findViewById(R.id.outdoor);
        indoor = (RadioButton) findViewById(R.id.indoor);
        ppp2 = (RadioButton) findViewById(R.id.ppp2);
        ppp3 = (RadioButton) findViewById(R.id.ppp3);
        RadioButton ppp6 = (RadioButton) findViewById(R.id.ppp6);
        bow = (Spinner) findViewById(R.id.bow);
        bow.setAdapter(new BowItemAdapter(this));
        target = (Spinner) findViewById(R.id.target_spinner);
        target.setAdapter(new TargetItemAdapter(this));
        addBow = (Button) findViewById(R.id.add_bow);
        addBow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NewRoundActivity.this, EditBowActivity.class);
                startActivity(i);
            }
        });
        Button new_round = (Button) findViewById(R.id.new_round_button);
        new_round.setText(getString(mTraining == -1 ? R.string.start :
                (mRound == -1 ? R.string.new_round : R.string.save)));
        new_round.setOnClickListener(this);
        Button cancel = (Button) findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (mRound == -1) {
            // Initialise with default values
            int distVal = prefs.getInt("distance", 10);
            int distanceInd = -1;
            for (int j = 0; j < NewRoundActivity.distanceValues.length; j++)
                if (NewRoundActivity.distanceValues[j] == distVal)
                    distanceInd = j;
            if(distanceInd==-1) {
                distance.setVisibility(View.GONE);
                customDist.setVisibility(View.VISIBLE);
                distanceVal.setText(""+distVal);
                custom = true;
            } else {
                distance.setSelection(distanceInd);
                distance.setVisibility(View.VISIBLE);
                customDist.setVisibility(View.GONE);
                custom = false;
            }
            indoor.setChecked(prefs.getBoolean("indoor", false));
            outdoor.setChecked(!prefs.getBoolean("indoor", false));
            int ppp = prefs.getInt("ppp", 3);
            ppp2.setChecked(ppp == 2);
            ppp3.setChecked(ppp == 3);
            ppp6.setChecked(ppp == 6);
            bow.setSelection(prefs.getInt("bow", 0));
            target.setSelection(prefs.getInt("target", 2));
            training.setText(getString(R.string.training));
        } else {
            // Load saved values
            DatabaseManager db = new DatabaseManager(this);
            Round r = db.getRound(mRound);
            if(r.distanceInd==-1) {
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
            ppp2.setChecked(r.ppp == 2);
            ppp3.setChecked(r.ppp == 3);
            ppp6.setChecked(r.ppp == 6);
            bow.setSelection(r.bow);
            target.setSelection(r.target);
            training.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bow.setAdapter(new BowItemAdapter(this));
        if (bow.getAdapter().getCount() > 0) {
            addBow.setVisibility(View.GONE);
            bow.setVisibility(View.VISIBLE);
        } else {
            addBow.setVisibility(View.VISIBLE);
            bow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(final View view) {
        int tar = target.getSelectedItemPosition();

        if (bow.getAdapter().getCount() == 0 && mBowType == -1 && tar == 4) {
            new AlertDialog.Builder(this).setTitle(R.string.title_compound)
                    .setMessage(R.string.msg_compound_type)
                    .setPositiveButton(R.string.compound_bow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBowType = 1;
                            NewRoundActivity.this.onClick(view);
                        }
                    })
                    .setNegativeButton(R.string.other_bow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBowType = 0;
                            NewRoundActivity.this.onClick(view);
                        }
                    })
                    .show();
            return;
        }

        String title = training.getTextString();
        DatabaseManager db = new DatabaseManager(this);
        if (mTraining == -1) {
            mTraining = db.newTraining(title);
        }
        long b = bow.getSelectedItemId();
        if (bow.getAdapter().getCount() == 0) {
            if (tar == 4) {
                b = mBowType == 1 ? -2 : -1;
            } else {
                b = -1;
            }
        }
        int dist;
        if(custom) {
            dist = Integer.parseInt(distanceVal.getText().toString());
        } else {
            dist = distanceValues[distance.getSelectedItemPosition()];
        }
        String unit = "m";
        int p = ppp2.isChecked() ? 2 : (ppp3.isChecked() ? 3 : 6);
        boolean in = indoor.isChecked();
        long round = db.newRound(mTraining, mRound, dist, unit, in, p, tar, b);
        db.close();

        SharedPreferences prefs = getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("bow", bow.getSelectedItemPosition());
        editor.putInt("distance", dist);
        editor.putInt("ppp", p);
        editor.putInt("target", tar);
        editor.putBoolean("indoor", in);
        editor.apply();

        finish();
        if (mRound == -1) {
            Intent i = new Intent(this, RoundActivity.class);
            i.putExtra(RoundActivity.ROUND_ID, round);
            i.putExtra(RoundActivity.TRAINING_ID, mTraining);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

            i = new Intent(this, PasseActivity.class);
            i.putExtra(PasseActivity.ROUND_ID, round);
            startActivity(i);

            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        if (mCalledFromPasse)
            overridePendingTransition(R.anim.right_in_half, R.anim.left_out_complete);
        else
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
