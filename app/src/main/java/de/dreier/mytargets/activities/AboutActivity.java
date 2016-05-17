package de.dreier.mytargets.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.AboutFragment;
import de.dreier.mytargets.fragments.DonateDialogFragment;

public class AboutActivity extends SimpleFragmentActivity implements BillingProcessor.IBillingHandler, DonateDialogFragment.DonationListener {

    public static final String DONATE = "donate";

    BillingProcessor bp;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DonateDialogFragment newFragment = new DonateDialogFragment();
            newFragment.show(getSupportFragmentManager(), "dialog");
        }
    };

    @Override
    protected Fragment instantiateFragment() {
        return new AboutFragment();
    }

    @Override
    public void onDonate(int position) {
        final String item = DonateDialogFragment.donations.get(position);
        if (position < 4) {
            bp.purchase(this, item);
        } else {
            bp.subscribe(this, item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DONATE);
        registerReceiver(receiver, filter);

        bp = new BillingProcessor(this, getString(R.string.BASE64_PUB_KEY), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }


    // IBillingHandler implementation

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        List<SkuDetails> inventory = bp.getPurchaseListingDetails(DonateDialogFragment.donations);

        for (String sku : DonateDialogFragment.donations) {
            Purchase purchase = inventory.getPurchase(sku);
            final SkuDetails skuDetails = inventory.getSkuDetails(sku);
            if (skuDetails == null) {
                continue;
            }
            DonateDialogFragment.prices.put(sku, skuDetails.getPrice());

            // If consumption failed last time try it again
            if (purchase != null && !sku.equals(DonateDialogFragment.DONATION_INFINITE)) {
                if (purchase.getOrderId() != null) {
                    mHelper.consumeAsync(inventory.getPurchase(sku), mConsumeFinishedListener);
                }
            }
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        bp.consumePurchase(productId);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("donated", true).apply();

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.donation_thank))
                .setNeutralButton(android.R.string.ok, null)
                .setCancelable(false)
                .create().show();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }
}
