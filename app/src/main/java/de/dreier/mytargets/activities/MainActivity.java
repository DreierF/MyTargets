package de.dreier.mytargets.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.TargetOpenHelper;
import de.dreier.mytargets.adapters.TrainingAdapter;

/**
 * Shows an overview over all trying days
 */
public class MainActivity extends NowListActivity {

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.training_singular);
        itemPlural = getString(R.string.training_plural);
        mEnableBackAnimation = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new TrainingAdapter(this);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db = new TargetOpenHelper(MainActivity.this);

                        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
                        String fileName = "/MyTargets/exported_data_" + format.format(new Date()) + ".csv";
                        File file = new File(baseDir + fileName);
                        try {
                            db.exportAll(file);
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                            email.setType("text/csv");
                            startActivity(Intent.createChooser(email, getString(R.string.send_exported)));
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, R.string.exporting_failed, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deleteTrainings(ids);
    }

    @Override
    public boolean onItemClick(Intent i, int pos, long id) {
        if (pos == 0) {
            i.setClass(this, NewRoundActivity.class);
        } else if (pos == 1) {
            i.setClass(this, BowActivity.class);
        } else {
            i.setClass(this, TrainingActivity.class);
            i.putExtra(TrainingActivity.TRAINING_ID, getListAdapter().getItemId(pos));
        }
        return true;
    }
}
