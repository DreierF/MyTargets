package de.dreier.mytargets.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.PasseAdapter;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.utils.TargetImage;

/**
 * Shows all passes of one round
 */
public class RoundActivity extends NowListActivity {

    private long mTraining;
    private long mRound;

    private Round mRoundInfo;

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
        return true;
    }

    private Intent buildShareIntent() {
        // Construct share intent
        mRoundInfo = db.getRound(mRound);
        int max = Target.getMaxPoints(mRoundInfo.target);
        int reached = db.getRoundPoints(mRound);
        int maxP = mRoundInfo.ppp * max * db.getPasses(mRound).getCount();
        String text = String.format(getString(R.string.my_share_text),
                mRoundInfo.scoreCount[0], mRoundInfo.scoreCount[1], mRoundInfo.scoreCount[2], reached, maxP);
        File dir = getExternalCacheDir();
        try {
            final File f = File.createTempFile("target", ".png", dir);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileOutputStream fOut;
                    try {
                        fOut = new FileOutputStream(f);
                        new TargetImage().generateBitmap(RoundActivity.this, 800, mRoundInfo, mRound, fOut);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            shareIntent.setType("*/*");
            return shareIntent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean hasPasses = adapter.getCount()>2;
        menu.findItem(R.id.action_scoreboard).setVisible(hasPasses);
        menu.findItem(R.id.action_share).setVisible(hasPasses);
        menu.findItem(R.id.action_statistics).setVisible(hasPasses);
        return super.onPrepareOptionsMenu(menu);
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
            case R.id.action_share:
                startActivity(buildShareIntent());
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
        supportInvalidateOptionsMenu();
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
