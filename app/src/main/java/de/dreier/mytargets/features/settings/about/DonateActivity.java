/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.settings.about;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsManager;
import im.delight.android.languages.Language;

public class DonateActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

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
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE);
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
            if (details != null && details.purchaseInfo.purchaseData.orderId != null) {
                bp.consumePurchase(sku.productId);
            }
        }

        new MaterialDialog.Builder(this)
                .title(R.string.donate)
                .adapter(new DonationAdapter(this, this::onDonate), new LinearLayoutManager(this))
                .dismissListener(dialog1 -> {
                    finish();
                    overridePendingTransition(0, 0);
                })
                .show();
    }

    public void onDonate(int position) {
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

        SettingsManager.setDonated(true);

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
