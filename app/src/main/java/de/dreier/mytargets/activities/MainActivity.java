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
import android.view.View;
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
import util.Purchase;

/**
 * Shows an overview over all trying days
 */
public class MainActivity extends ActionBarActivity implements DonateDialogFragment.DonationListener {

    private static final String TAG = "main";
    public static final String BASE64_PUB_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvxNm9AWZrsRqDB04Uic0rJSmsCBlRcDA1OnNPtC14Eth5iy4dTlPKN1HNOUfz+2mqvq4cylrb/NTxpkZSQ1NwLHMqG3pdzJAbwnr7MJoqhum3MKeDxuuM6ptZP8EucPEcFwcGQWQAD5URLFuT2nk9Ezws2TU2EsGnTI97Dpdonv/ans/2NuUz04KW2IrNLuMZp20h+3uekp7CwP1mjs7cqrL63WzEl5cT+RjfFGUH8SpdDvO71duLefnBS/vHftf+tVpv+vD12C+BlfN8Dun3R9EM8QqM32VB6M8ycJcEcmkz+U6IfMu/ShQapOctUCFkud9Bd55tXXnJyoQXEt0YwIDAQAB";
    private static boolean shownThisTime = false;
    private ViewPager viewPager;

    // In-app billing
    private IabHelper mHelper;
    static final int RC_REQUEST = 10001;
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
        mHelper = new IabHelper(this, BASE64_PUB_KEY);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");
                mInfiniteSupported = mHelper.subscriptionsSupported();
            }
        });
    }

    private void askForHelpTranslating() {
        ArrayList<String> supportedLanguages = new ArrayList<>();
        supportedLanguages.add("de");
        supportedLanguages.add("en");
        supportedLanguages.add("fr");
        supportedLanguages.add("es");

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean shown = prefs.getBoolean("translation_dialog_shown", false);

        String longLang = Locale.getDefault().getDisplayLanguage();
        String shortLocale = Locale.getDefault().getLanguage();
        if (!supportedLanguages.contains(shortLocale) && !shown && !shownThisTime) {
            // Link the e-mail address in the message
            final SpannableString s = new SpannableString(Html.fromHtml("If you would like " +
                    "to help make MyTargets even better by translating the app to " +
                    longLang + ", please send me an E-Mail (dreier.florian@gmail.com) " +
                    "so I can give you access to the translation tool!<br /><br />" +
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
            ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
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
            case R.id.action_backup:
                BackupUtils.Backup(this);
                return true;
            case R.id.action_import:
                Intent getContentIntent = FileUtils.createGetContentIntent();
                Intent intent = Intent.createChooser(getContentIntent, getString(R.string.select_a_file));
                startActivityForResult(intent, 1);
                return true;
            case R.id.action_donate:
                DonateDialogFragment newFragment = DonateDialogFragment.newInstance(mInfiniteSupported);
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

    void complain(String message) {
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton(android.R.string.ok, null);
        bld.create().show();
    }

    @Override
    public void onDonate(int position) {
        if (position < 4) {
            mHelper.launchPurchaseFlow(this, DonateDialogFragment.donation[position],
                    RC_REQUEST, mPurchaseFinishedListener, "");
        } else {
            mHelper.launchPurchaseFlow(this, DonateDialogFragment.donation[position],
                    IabHelper.ITEM_TYPE_SUBS,
                    RC_REQUEST, mPurchaseFinishedListener, "");
        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            alert(getString(R.string.donation_thank));
        }
    };
}
