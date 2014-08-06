package de.dreier.mytargets;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Shows an overview over all tring days
 * */
public class MainActivity extends ListActivity implements ListView.OnItemClickListener {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = getListView();
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(this);
        mListView.setBackgroundColor(0xFFEEEEEE);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        TrainingAdapter adapter = new TrainingAdapter(this);
        setListAdapter(adapter);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        if(pos==0) {
            Intent i = new Intent(this,NewRoundActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            Intent i = new Intent(this,TrainingActivity.class);
            i.putExtra(TrainingActivity.TRAINING_ID,getListAdapter().getItemId(pos));
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }
}
