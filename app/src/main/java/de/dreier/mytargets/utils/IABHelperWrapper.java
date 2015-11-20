/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.utils;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.DonateDialogFragment;
import util.IabHelper;
import util.IabResult;
import util.Inventory;
import util.Purchase;

// In-app billing
public class IABHelperWrapper {

    private IabHelper mHelper;
    private static final int RC_REQUEST = 10001;
    private boolean mInfiniteSupported;
    private boolean mSubscribedToInfinite;
    private static final String TAG = "iab";

    private final AppCompatActivity mContext;

    public IABHelperWrapper(AppCompatActivity context) {
        mContext = context;
        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(mContext, mContext.getString(R.string.BASE64_PUB_KEY));
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

    public void showDialog(Fragment fragment) {
        DonateDialogFragment newFragment = DonateDialogFragment
                .newInstance(mInfiniteSupported, mSubscribedToInfinite);
        newFragment.setTargetFragment(fragment, 1);
        newFragment.show(fragment.getFragmentManager(), "dialog");
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mHelper == null || !mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    // We're being destroyed. It's important to dispose of the helper here!
    public void onDestroy() {
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    private final IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null || result == null) {
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

    public void startDonationForItem(int position) {
        if (position < 4) {
            mHelper.launchPurchaseFlow(mContext, DonateDialogFragment.donations.get(position),
                    RC_REQUEST, mPurchaseFinishedListener, "");
        } else {
            mHelper.launchPurchaseFlow(mContext, DonateDialogFragment.donations.get(position),
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
                    .getDefaultSharedPreferences(mContext);
            prefs.edit().putBoolean("donated", true).apply();

            new AlertDialog.Builder(mContext)
                    .setMessage(mContext.getString(R.string.donation_thank))
                    .setNeutralButton(android.R.string.ok, null)
                    .setCancelable(false)
                    .create().show();
        }
    };
}
