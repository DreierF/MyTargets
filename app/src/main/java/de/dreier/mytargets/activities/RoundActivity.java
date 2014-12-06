package de.dreier.mytargets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.PasseAdapter;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.utils.TargetOpenHelper;

/**
 * Shows all passes of one round
 */
public class RoundActivity extends NowListActivity {

    private long mTraining;
    private long mRound;

    private TargetOpenHelper.Round mRoundInfo;

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.passe_singular);
        itemPlural = getString(R.string.passe_plural);
        if (intent != null && intent.hasExtra(ROUND_ID)) {
            mTraining = intent.getLongExtra(TRAINING_ID, -1);
            mRound = intent.getLongExtra(ROUND_ID, -1);
        }
        if (savedInstanceState != null) {
            mTraining = savedInstanceState.getLong(TRAINING_ID, -1);
            mRound = savedInstanceState.getLong(ROUND_ID, -1);
        }

        setTitle(getString(R.string.round) + " " + db.getRoundInd(mTraining, mRound));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.round, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Construct share intent
        mRoundInfo = db.getRound(mRound);
        int max = Target.getMaxPoints(mRoundInfo.target);
        int reached = db.getRoundPoints(mRound);
        int maxP = mRoundInfo.ppp * max * db.getPasses(mRound).getCount();
        String text = String.format(getString(R.string.my_share_text),
                mRoundInfo.scoreCount[0], mRoundInfo.scoreCount[1], mRoundInfo.scoreCount[2], reached, maxP);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");
        shareActionProvider.setShareIntent(shareIntent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scoreboard:
                Intent intent = new Intent(this, ScoreboardActivity.class);
                intent.putExtra(ScoreboardActivity.ROUND_ID, mRound);
                startActivity(intent);
                return true;
            case R.id.action_statistics:
                Intent i = new Intent(this, StatisticsActivity.class);
                i.putExtra(StatisticsActivity.ROUND_ID, mRound);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deletePasses(ids);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRoundInfo = db.getRound(mRound);
        adapter = new PasseAdapter(this, mTraining, mRound, mRoundInfo);
        setListAdapter(adapter);
    }

    @Override
    public boolean onItemClick(Intent i, int pos, long id) {
        if (pos == 0) {
            i.setClass(this, PasseActivity.class);
            i.putExtra(PasseActivity.ROUND_ID, mRound);
        } else if (pos == 1) {
            return false;
        } else {
            i.setClass(this, PasseActivity.class);
            i.putExtra(PasseActivity.ROUND_ID, mRound);
            i.putExtra(PasseActivity.PASSE_IND, pos);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TRAINING_ID, mTraining);
        outState.putLong(ROUND_ID, mRound);
    }
}
