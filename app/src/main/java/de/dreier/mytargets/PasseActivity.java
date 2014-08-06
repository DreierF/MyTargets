package de.dreier.mytargets;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class PasseActivity extends Activity implements TargetView.OnTargetSetListener {

    private TargetView target;
    private Button next, prev;
    private int curPasse = 1;
    private int savedPasses = 0;
    private int mRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_targets);

        target = (TargetView)findViewById(R.id.targetview);
        target.setOnTargetSetListener(this);
        next = (Button)findViewById(R.id.next_button);
        prev = (Button)findViewById(R.id.prev_button);

        if(savedInstanceState!=null) {
            target.restoreState(savedInstanceState);
            curPasse = savedInstanceState.getInt("curPasse");
        }
        setPasse(curPasse);

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
        if(passe!=curPasse) {
            TargetOpenHelper db = new TargetOpenHelper(this);
            int[] points = db.getPasse(mRound,passe);
            if(points!=null)
                target.setPoints(points);
            else
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
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
    public void OnTargetSet(int[] points) {
        TargetOpenHelper db = new TargetOpenHelper(this);

        if(curPasse>savedPasses) {
            savedPasses++;
            db.addPasseToRound(mRound, points);
        } else {
            //db.updatePasse(mRound, points);
        }
        db.close();
        setPasse(curPasse);
    }
}
