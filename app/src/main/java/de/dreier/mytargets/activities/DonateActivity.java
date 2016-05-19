package de.dreier.mytargets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.DonationAdapter;

public class DonateActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    public static final ArrayList<String> donations;
    public static final HashMap<String, String> prices;

    static {
        donations = new ArrayList<>();
        donations.add("donation_2");
        donations.add("donation_5");
        donations.add("donation_10");
        donations.add("donation_20");
        prices = new HashMap<>();
    }

    private BillingProcessor bp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bp = new BillingProcessor(this, getString(R.string.BASE64_PUB_KEY), this);
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }

        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // IBillingHandler implementation

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        List<SkuDetails> inventory = bp.getPurchaseListingDetails(donations);
        for (SkuDetails sku : inventory) {
            prices.put(sku.productId, sku.priceText);
            TransactionDetails details = bp.getPurchaseTransactionDetails(sku.productId);

            // If consumption failed last time try it again
            if (details != null && details.orderId != null) {
                bp.consumePurchase(sku.productId);
            }
        }

        new MaterialDialog.Builder(this)
                .title(R.string.donate)
                .adapter(new DonationAdapter(this),
                        (dialog, itemView, position, text) -> onDonate(position))
                .dismissListener(dialog1 -> {
                    finish();
                    overridePendingTransition(0, 0);
                })
                .show();
    }

    private void onDonate(int position) {
        final String item = donations.get(position);
        if (position < 4) {
            bp.purchase(this, item);
        } else {
            bp.subscribe(this, item);
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        bp.consumePurchase(productId);

        // TODO migrate to SettingsManager
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("donated", true).apply();

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.donation_thank))
                .setNeutralButton(android.R.string.ok, null)
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         */
        if (error != null) {
            error.printStackTrace();
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }
}
