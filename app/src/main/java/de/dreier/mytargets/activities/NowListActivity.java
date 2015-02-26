package de.dreier.mytargets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.PluralsRes;
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

import com.melnykov.fab.FloatingActionButton;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.DatabaseManager;

/**
 * Shows all rounds of one settings_only day
 */
public abstract class NowListActivity extends ActionBarActivity implements ListView.OnItemClickListener, View.OnClickListener {

    public static final String TRAINING_ID = "training_id";
    public static final String ROUND_ID = "round_id";
    protected ListView mListView;
    NowListAdapter adapter;
    protected @PluralsRes int itemTypeRes;
    DatabaseManager db;
    boolean mEnableBackAnimation = true;
    boolean mEditable = false;

    protected int getLayoutResource() {
        return R.layout.fragment_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        db = DatabaseManager.getInstance(this);

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

                final String title = getResources().getQuantityString(itemTypeRes, count, count);
                mode.setTitle(title);
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
        init(getIntent(), savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab.attachToListView(mListView);
    }

    void onEdit(long id) {
    }

    void setListAdapter(NowListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    ListAdapter getListAdapter() {
        return mListView.getAdapter();
    }

    protected abstract void init(Intent intent, Bundle savedInstanceState);

    protected abstract void onDelete(long[] ids);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    protected abstract boolean onItemClick(Intent i, int pos, long id);

    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        if (onItemClick(i, 0, 0)) {
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }
}
