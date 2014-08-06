package de.dreier.mytargets;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.DateFormat;

/**
 * Shows all rounds of one training day
 */
public class TrainingActivity extends ListActivity implements ListView.OnItemClickListener {

    public static final String TRAINING_ID = "training_id";
    private ListView mListView;
    private long mTraining;
    private RundenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if(i!=null && i.hasExtra(TRAINING_ID)) {
            mTraining = i.getLongExtra(TRAINING_ID,-1);
        }
        if(savedInstanceState!=null) {
            mTraining = savedInstanceState.getLong(TRAINING_ID,-1);
        }
        if(mTraining==-1)
            finish();

        TargetOpenHelper db = new TargetOpenHelper(this);
        TargetOpenHelper.Training tr = db.getTraining(mTraining);
        getActionBar().setTitle(tr.title);
        getActionBar().setSubtitle(DateFormat.getDateInstance().format(tr.date));

        mListView = getListView();
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(this);
        mListView.setBackgroundColor(0xFFEEEEEE);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new RundenAdapter(this,mTraining);
        setListAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TRAINING_ID,mTraining);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.right_out, R.anim.left_in);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        if(pos==0) {
            Intent i = new Intent(this,NewRoundActivity.class);
            i.putExtra(NewRoundActivity.TRAINING_ID,mTraining);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            Intent i = new Intent(this,RundeActivity.class);
            i.putExtra(RundeActivity.RUNDE_ID,getListAdapter().getItemId(pos));
            i.putExtra(RundeActivity.TRAINING_ID,mTraining);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }
}
