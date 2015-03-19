package de.dreier.mytargets.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.MainTabsFragmentPagerAdapter;
import de.dreier.mytargets.fragments.DonateDialogFragment;
import de.dreier.mytargets.utils.BackupUtils;
import de.dreier.mytargets.views.SlidingTabLayout;
import util.IabHelper;
import util.IabResult;
import util.Inventory;
import util.Purchase;

/**
 * Shows an overview over all trying days
 */
public class MainActivity extends ActionBarActivity
        implements DonateDialogFragment.DonationListener {

    private static final String TAG = "main";
    private static boolean shownThisTime = false;
    private ViewPager viewPager;

    // In-app billing
    private IabHelper mHelper;
    private static final int RC_REQUEST = 10001;
    private boolean mInfiniteSupported;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainTabsFragmentPagerAdapter(this));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        //slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        askForHelpTranslating();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(this, getString(R.string.BASE64_PUB_KEY));
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    return;
                }

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up in-app billing: " + result);
                    return;
                }

                mInfiniteSupported = mHelper.subscriptionsSupported();
                mHelper.queryInventoryAsync(true, DonateDialogFragment.donations,
                                            mGotInventoryListener);
            }
        });
    }

    private void askForHelpTranslating() {
        ArrayList<String> supportedLanguages = new ArrayList<>();
        supportedLanguages.add("de");
        supportedLanguages.add("en");
        supportedLanguages.add("fr");
        supportedLanguages.add("es");
        supportedLanguages.add("ru");
        supportedLanguages.add("nl");

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        boolean shown = prefs.getBoolean("translation_dialog_shown", false);

        String longLang = Locale.getDefault().getDisplayLanguage();
        String shortLocale = Locale.getDefault().getLanguage();
        if (!supportedLanguages.contains(shortLocale) && !shown && !shownThisTime) {
            // Link the e-mail address in the message
            final SpannableString s = new SpannableString(Html.fromHtml("If you would like " +
                                                                                "to help make MyTargets even better by translating the app to " +
                                                                                longLang +
                                                                                ", please send me an E-Mail (dreier.florian@gmail.com) " +
                                                                                "so I can give you access to the translation file!<br /><br />" +
                                                                                "Thanks in advance :)"));
            Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES);
            AlertDialog d = new AlertDialog.Builder(this).setTitle("App translation")
                    .setMessage(s)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prefs.edit().putBoolean("translation_dialog_shown", true).apply();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Remind me later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shownThisTime = true;
                            dialog.dismiss();
                        }
                    }).create();
            d.show();
            ((TextView) d.findViewById(android.R.id.message))
                    .setMovementMethod(LinkMovementMethod.getInstance());
        }
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
                        try {
                            Uri uri = BackupUtils.export(getApplicationContext());
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_STREAM, uri);
                            email.setType("text/csv");
                            startActivity(
                                    Intent.createChooser(email, getString(R.string.send_exported)));
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, R.string.exporting_failed,
                                                   Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
                return true;
            case R.id.action_backup:
                BackupUtils.Backup(this);
                return true;
            case R.id.action_import:
                Intent getContentIntent = FileUtils.createGetContentIntent();
                Intent intent = Intent
                        .createChooser(getContentIntent, getString(R.string.select_a_file));
                startActivityForResult(intent, 1);
                return true;
            case R.id.action_donate:
                DonateDialogFragment newFragment = DonateDialogFragment
                        .newInstance(mInfiniteSupported, mSubscribedToInfinite);
                newFragment.show(getSupportFragmentManager(), "dialog");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Pass on the activity result to the helper for handling
        if (mHelper == null || !mHelper.handleActivityResult(requestCode, resultCode, data)) {
            if (requestCode == 1 && resultCode == ActionBarActivity.RESULT_OK) {
                final Uri uri = data.getData();
                if (BackupUtils.Import(this, uri)) {
                    viewPager.setAdapter(new MainTabsFragmentPagerAdapter(this));
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    private boolean mSubscribedToInfinite;

    // Listener that's called when we finish querying the items and subscriptions we own
    private final IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                return;
            }

            // Is it a failure?
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;
            }

            // Do we have the infinite subscription
            Purchase infinitePurchase = inventory
                    .getPurchase(DonateDialogFragment.DONATION_INFINITE);
            mSubscribedToInfinite =
                    infinitePurchase != null && infinitePurchase.getOrderId() != null;

            for (String sku : DonateDialogFragment.donations) {
                Purchase purchase = inventory.getPurchase(sku);
                DonateDialogFragment.prices.put(sku, inventory.getSkuDetails(sku).getPrice());

                // If consumption failed last time try it again
                if (purchase != null && !sku.equals(DonateDialogFragment.DONATION_INFINITE)) {
                    if (purchase.getOrderId() != null) {
                        mHelper.consumeAsync(inventory.getPurchase(sku), mConsumeFinishedListener);
                    }
                }
            }
        }
    };

    @Override
    public void onDonate(int position) {
        if (position < 4) {
            mHelper.launchPurchaseFlow(this, DonateDialogFragment.donations.get(position),
                                       RC_REQUEST, mPurchaseFinishedListener, "");
        } else {
            mHelper.launchPurchaseFlow(this, DonateDialogFragment.donations.get(position),
                                       IabHelper.ITEM_TYPE_SUBS,
                                       RC_REQUEST, mPurchaseFinishedListener, "");
        }
    }

    // Callback for when a purchase is finished
    private final IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                return;
            }

            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            }

            if (!purchase.getSku().equals(DonateDialogFragment.DONATION_INFINITE)) {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(MainActivity.this);
            prefs.edit().putBoolean("donated", true).apply();

            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(getString(R.string.donation_thank))
                    .setNeutralButton(android.R.string.ok, null)
                    .setCancelable(false)
                    .create().show();
        }
    };

    // Called when consumption is complete
    private final IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (mHelper == null) {
                return;
            }

            if (!result.isSuccess()) {
                Log.d(TAG, "Error while consuming: " + result);
            }

        }
    };
}
