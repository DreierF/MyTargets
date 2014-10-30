package de.dreier.mytargets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

public class NewRoundActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String TRAINING_ID = "training_id";
    public static final String FROM_PASSE = "from_passe";
    private long mTraining = -1;

    private Spinner distance;
    private RadioButton outdoor,indoor;
    private Spinner bow,target;
    public static String[] distances = {"10m","15m", "18m", "20m", "25m", "30m", "40m", "50m", "60m", "70m", "90m"};
    public static int[] distanceValues = {10, 15, 18, 20, 25, 30, 40, 50, 60, 70, 90};
    private RadioButton ppp3;
    private Button addBow;
    private boolean mCalledFromPasse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_round);

        Intent i = getIntent();
        if (i != null) {
            if (i.hasExtra(TRAINING_ID)) {
                mTraining = i.getLongExtra(TRAINING_ID, -1);
            }
            mCalledFromPasse = i.hasExtra(FROM_PASSE);
        }
        SharedPreferences prefs = getSharedPreferences(MyBackupAgent.PREFS, 0);

        distance = (Spinner) findViewById(R.id.distance);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(distance.getContext(),
                android.R.layout.simple_spinner_item, distances);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distance.setAdapter(adapter);
        distance.setSelection(prefs.getInt("distance",0));

        outdoor = (RadioButton) findViewById(R.id.outdoor);
        indoor = (RadioButton) findViewById(R.id.indoor);
        if(prefs.getBoolean("indoor",false)) {
            indoor.setChecked(true);
        } else {
            outdoor.setChecked(true);
        }

        ppp3 = (RadioButton) findViewById(R.id.ppp3);
        RadioButton ppp6 = (RadioButton) findViewById(R.id.ppp6);
        if(prefs.getInt("ppp",3)==3) {
            ppp3.setChecked(true);
        } else {
            ppp6.setChecked(true);
        }

        bow = (Spinner) findViewById(R.id.bow);
        bow.setAdapter(new BowItemAdapter(this));
        bow.setSelection(prefs.getInt("bow",0));

        target = (Spinner) findViewById(R.id.target_spinner);
        target.setAdapter(new TargetItemAdapter(this));
        target.setSelection(prefs.getInt("target",2));
        addBow = (Button) findViewById(R.id.add_bow);
        addBow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NewRoundActivity.this,EditBowActivity.class);
                startActivity(i);
            }
        });
        Button new_round = (Button) findViewById(R.id.new_round_button);
        new_round.setText(getString(mTraining==-1?R.string.start:R.string.new_round));
        new_round.setOnClickListener(this);
        Button cancel = (Button) findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bow.setAdapter(new BowItemAdapter(this));
        if(bow.getAdapter().getCount()>0) {
            addBow.setVisibility(View.GONE);
            bow.setVisibility(View.VISIBLE);
        } else {
            addBow.setVisibility(View.VISIBLE);
            bow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        TargetOpenHelper db = new TargetOpenHelper(NewRoundActivity.this);
        if(mTraining==-1) {
            mTraining = db.newTraining();
        }
        long b = bow.getSelectedItemId();
        int dist = distance.getSelectedItemPosition();
        String unit = "m";
        int p = ppp3.isChecked()?3:6;
        int tar = target.getSelectedItemPosition();
        boolean in = indoor.isChecked();
        long round = db.newRound(mTraining, dist, unit, in, p, tar, b);
        db.close();

        SharedPreferences prefs = getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("bow",bow.getSelectedItemPosition());
        editor.putInt("distance",dist);
        editor.putInt("ppp",p);
        editor.putInt("target",tar);
        editor.putBoolean("indoor",in);
        editor.apply();

        Intent i = new Intent(this,RoundActivity.class);
        i.putExtra(RoundActivity.ROUND_ID,round);
        i.putExtra(RoundActivity.TRAINING_ID,mTraining);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);

        i = new Intent(this,PasseActivity.class);
        i.putExtra(PasseActivity.ROUND_ID,round);
        startActivity(i);

        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        finish();
        if(mCalledFromPasse)
            overridePendingTransition(R.anim.right_in_half, R.anim.left_out_complete);
        else
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
