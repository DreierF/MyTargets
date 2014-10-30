package de.dreier.mytargets;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Shows all rounds of one settings_only day
 */
public abstract class NowListActivity extends ActionBarActivity implements ListView.OnItemClickListener {

    public static final String TRAINING_ID = "training_id";
    public static final String ROUND_ID = "round_id";
    protected ListView mListView;
    protected NowListAdapter adapter;
    protected String itemSingular;
    protected String itemPlural;
    protected TargetOpenHelper db;
    protected boolean mEnableBackAnimation = true;
    protected boolean mEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        db = new TargetOpenHelper(this);
        init(getIntent(), savedInstanceState);

        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(this);
        mListView.setBackgroundColor(0xFFEEEEEE);
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int count = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (!adapter.isSelectable(position)) {
                    if (checked)
                        mListView.setItemChecked(position, false);
                    return;
                }
                count += checked ? 1 : -1;

                if (count == 1)
                    mode.setTitle("1 " + itemSingular + " " + getString(R.string.selected));
                else
                    mode.setTitle(count + " " + itemPlural + " " + getString(R.string.selected));
                mode.invalidate();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        long id = mListView.getCheckedItemIds()[0];
                        onEdit(id);
                        onResume();
                        mode.finish();
                        return true;
                    case R.id.action_delete:
                        long[] ids = mListView.getCheckedItemIds();
                        onDelete(ids);
                        onResume();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu_edit_delete, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                count = 0;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem edit = menu.findItem(R.id.action_edit);
                edit.setVisible(count == 1 && mEditable);
                return false;
            }
        });
    }

    protected void onEdit(long id) {}

    protected void setListAdapter(NowListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    protected ListAdapter getListAdapter() {
        return mListView.getAdapter();
    }

    protected abstract void init(Intent intent, Bundle savedInstanceState);

    protected abstract void onDelete(long[] ids);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_settings:
                Intent i = new Intent(this,SettingsActivity.class);
                startActivity(i);
                return true;*/
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        Intent i = new Intent();
        if (onItemClick(i, pos, id)) {
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mEnableBackAnimation)
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public abstract boolean onItemClick(Intent i, int pos, long id);
}
