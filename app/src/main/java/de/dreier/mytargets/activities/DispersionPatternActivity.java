package de.dreier.mytargets.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityArrowRankingDetailsBinding;
import de.dreier.mytargets.models.ArrowStatistic;
import de.dreier.mytargets.utils.TargetImage;
import de.dreier.mytargets.utils.ToolbarUtils;

import static android.support.v4.content.FileProvider.getUriForFile;

public class DispersionPatternActivity extends ChildActivityBase {

    public static final String ITEM = "item";
    private ActivityArrowRankingDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil
                .setContentView(this, R.layout.activity_arrow_ranking_details);

        ArrowStatistic item = Parcels.unwrap(getIntent().getParcelableExtra(ITEM));
        binding.dispersionView.setShoots(item.shots);
        binding.dispersionView.setTarget(item.target.getDrawable());

        ToolbarUtils.showHomeAsUp(this);
        ToolbarUtils.setTitle(this, getString(R.string.arrow_number_x, item.arrowNumber));
        ToolbarUtils.setSubtitle(this, item.arrowName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scoreboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareImage();
                return true;
            //case R.id.action_print:
            //    print();
            //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Called after the user selected with items he wants to share */
    private void shareImage() {
        // Construct share intent
        new Thread(() -> {
            try {
                File dir = getCacheDir();
                final File f = File.createTempFile("dispersion_pattern", ".png", dir);
                new TargetImage().generateTrainingBitmap(800, 0, f); //TODO set training id

                // Build and fire intent to ask for share provider
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, getUriForFile(DispersionPatternActivity.this, "de.dreier.mytargets", f));
                shareIntent.setType("*/*");
                startActivity(shareIntent);
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(binding.getRoot(), R.string.sharing_failed, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }).start();
    }
}
