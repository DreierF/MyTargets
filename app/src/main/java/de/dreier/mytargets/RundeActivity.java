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

import de.dreier.mytargets.R;

/**
 * Shows all passes of one round
 */
public class RundeActivity extends ListActivity implements ListView.OnItemClickListener {

    public static final String RUNDE_ID = "runde_id";
    private long mRound;
    private PasseAdapter adapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if(i!=null && i.hasExtra(RUNDE_ID)) {
            mRound = i.getLongExtra(RUNDE_ID,-1);
        }

        mListView = getListView();
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(this);
        mListView.setBackgroundColor(0xFFEEEEEE);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.runde, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_start_passe:
                i = new Intent(this,PasseActivity.class);
                startActivity(i);
                return true;
            case R.id.action_runde_detail:
                i = new Intent(this,EditRoundActivity.class);
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
    public void onResume() {
        super.onResume();
        adapter = new PasseAdapter(this,mRound);
        setListAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        if(pos==0) {
            Intent i = new Intent(this,PasseActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }
}
