package de.dreier.mytargets.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.print.PrintHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityArrowRankingDetailsBinding;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.models.ArrowStatistic;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.utils.DistributionPatternUtils;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;

import static android.support.v4.content.FileProvider.getUriForFile;

public class DispersionPatternActivity extends ChildActivityBase {

    public static final String ITEM = "item";
    public static final String ROUND_IDS = "round_ids";
    private ActivityArrowRankingDetailsBinding binding;
    private long[] roundIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil
                .setContentView(this, R.layout.activity_arrow_ranking_details);

        ArrowStatistic item = Parcels.unwrap(getIntent().getParcelableExtra(ITEM));
        roundIds = getIntent().getLongArrayExtra(ROUND_IDS);
        binding.dispersionView.setShoots(item.shots);
        binding.dispersionView.setTarget(item.target.getDrawable());

        ToolbarUtils.showHomeAsUp(this);
        if (item.arrowName != null) {
            ToolbarUtils.setTitle(this, getString(R.string.arrow_number_x, item.arrowNumber));
            ToolbarUtils.setSubtitle(this, item.arrowName);
        } else {
            ToolbarUtils.setTitle(this, R.string.dispersion_pattern);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scoreboard, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareImage();
                return true;
            case R.id.action_print:
                print();
                return true;
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
                List<Round> rounds = Stream.of(Utils.toList(roundIds))
                        .map(id -> new RoundDataSource().get(id))
                        .collect(Collectors.toList());
                DistributionPatternUtils.createDistributionPatternImageFile(800, rounds, f);

                // Build and fire intent to ask for share provider
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("*/*");
                String packageName = getApplicationContext().getPackageName();
                String authority = packageName + ".easyphotopicker.fileprovider";
                shareIntent.putExtra(Intent.EXTRA_STREAM, getUriForFile(DispersionPatternActivity.this, authority, f));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(binding.getRoot(), R.string.sharing_failed, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }).start();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void print() {
        PrintHelper printHelper = new PrintHelper(this);
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);

        // Get the image
        List<Round> rounds = Stream.of(Utils.toList(roundIds))
                .map(id -> new RoundDataSource().get(id))
                .collect(Collectors.toList());
        Bitmap image = DistributionPatternUtils.getDistributionPatternBitmap(800, rounds);
        if (image != null) {
            // Send it to the print helper
            printHelper.printBitmap("MyTargets", image);
        }
    }
}
