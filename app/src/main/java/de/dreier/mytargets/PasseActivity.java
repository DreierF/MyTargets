package de.dreier.mytargets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class PasseActivity extends Activity implements TargetView.OnTargetSetListener {

    public static final String ROUND_ID = "round_id";
    public static final String PASSE_IND = "passe_ind";
    private TargetView target;
    private Button next, prev;
    private int curPasse = 1;
    private int savedPasses = 0;
    private long mRound, mTraining;
    private TargetOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_targets);

        db = new TargetOpenHelper(this);

        Intent i = getIntent();
        if(i!=null && i.hasExtra(ROUND_ID)) {
            mRound = i.getLongExtra(ROUND_ID,-1);
            savedPasses = db.getPasses(mRound).getCount();
            if(i.hasExtra(PASSE_IND)) {
                curPasse = i.getIntExtra(PASSE_IND, -1);
            } else {
                curPasse = savedPasses+1;
            }
        }

        target = (TargetView)findViewById(R.id.targetview);
        target.setOnTargetSetListener(this);
        next = (Button)findViewById(R.id.next_button);
        prev = (Button)findViewById(R.id.prev_button);

        TargetOpenHelper.Round r = db.getRound(mRound);
        mTraining = r.training;
        target.setTargetRound(r.target);
        target.setPPP(r.ppp);

        setPasse(curPasse);

        if(savedInstanceState!=null) {
            target.restoreState(savedInstanceState);
            curPasse = savedInstanceState.getInt("curPasse");
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasse(curPasse + 1);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasse(curPasse - 1);
            }
        });
    }

    public void setPasse(int passe) {
        if(passe<=savedPasses) {
            int[] points = db.getPasse(mRound,passe);
            if(points!=null)
                target.setZones(points);
            else
                target.reset();
        } else {
            target.reset();
        }
        curPasse = passe;
        setTitle("Passe "+curPasse);
        prev.setEnabled(curPasse > 1);
        next.setEnabled(curPasse<=savedPasses);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.passe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            /*case R.id.action_settings:
                i = new Intent(this,SettingsActivity.class);
                startActivity(i);
                return true;*/
            case R.id.action_new_runde:
                i = new Intent(this,NewRoundActivity.class);
                i.putExtra(NewRoundActivity.TRAINING_ID,mTraining);
                startActivity(i);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.right_out, R.anim.left_in);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curPasse",curPasse);
        target.saveState(outState);
    }

    @Override
    public void OnTargetSet(int[] zones) {
        TargetOpenHelper db = new TargetOpenHelper(this);

        if(curPasse>savedPasses) {
            savedPasses++;
            db.addPasseToRound(mRound, zones);
        } else {
            db.updatePasse(mRound, curPasse, zones);
        }
        db.close();
        setPasse(curPasse);
    }
}
