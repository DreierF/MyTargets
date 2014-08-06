package de.dreier.mytargets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;

public class NewRoundActivity extends Activity implements View.OnClickListener {

    public static final String TRAINING_ID = "training_id";
    private long mTraining = -1;

    private Spinner distance;
    private RadioButton outdoor,indoor;
    private NumberPicker ppp;
    private Spinner bow,target;
    private String[] distances = {"10m","15m", "18m", "20m", "25m", "30m", "40m", "50m", "60m", "70m", "90m"};
    private int[] distanceValues = {10, 15, 18, 20, 25, 30, 40, 50, 60, 70, 90};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_round);

        Intent i = getIntent();
        if (i != null) {
            if (i.hasExtra(TRAINING_ID)) {
                mTraining = i.getLongExtra(TRAINING_ID, -1);
            }
        }

        distance = (Spinner) findViewById(R.id.distance);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(distance.getContext(),
                android.R.layout.simple_spinner_item, distances);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distance.setAdapter(adapter);
        outdoor = (RadioButton) findViewById(R.id.outdoor);
        outdoor.setChecked(true);
        indoor = (RadioButton) findViewById(R.id.indoor);
        ppp = (NumberPicker) findViewById(R.id.ppp);
        ppp.setMaxValue(10);
        ppp.setMinValue(3);
        bow = (Spinner) findViewById(R.id.bow);
        target = (Spinner) findViewById(R.id.target);
        target.setAdapter(new TargetAdapter(this));
        Button cancel = (Button) findViewById(R.id.cancel_button);
        Button new_round = (Button) findViewById(R.id.new_round_button);
        new_round.setText(mTraining==-1?"Starten":"Neue Runde");
        new_round.setOnClickListener(this);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        TargetOpenHelper db = new TargetOpenHelper(NewRoundActivity.this);
        if(mTraining==-1) {
            mTraining = db.newTraining();
        }
        long bow = 0;
        int dist = distanceValues[distance.getSelectedItemPosition()];
        String unit = "m";
        int p = ppp.getValue();
        int tar = target.getSelectedItemPosition();
        boolean in = indoor.isChecked();
        long round = db.newRound(mTraining, dist, unit, in, p, tar, bow);
        db.close();
        Intent i = new Intent(this,RundeActivity.class);
        i.putExtra(RundeActivity.RUNDE_ID,round);
        i.putExtra(RundeActivity.TRAINING_ID,mTraining);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_round, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this,SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
