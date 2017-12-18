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

package de.dreier.mytargets.features.settings.about

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import de.dreier.mytargets.R
import de.dreier.mytargets.features.settings.SettingsManager
import im.delight.android.languages.Language
import java.util.*

class DonateActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {

    private lateinit var bp: BillingProcessor

    public override fun onCreate(savedInstanceState: Bundle?) {
        Language.setFromPreference(this, SettingsManager.KEY_LANGUAGE)
        super.onCreate(savedInstanceState)
        bp = BillingProcessor(this, getString(R.string.BASE64_PUB_KEY), this)
    }

    public override fun onDestroy() {
        bp.release()
        super.onDestroy()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // IBillingHandler implementation

    override fun onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        val inventory = bp.getPurchaseListingDetails(donations)
        for (sku in inventory) {
            prices.put(sku.productId, sku.priceText)
            val details = bp.getPurchaseTransactionDetails(sku.productId)

            // If consumption failed last time try it again
            if (details != null && details.purchaseInfo.purchaseData.orderId != null) {
                bp.consumePurchase(sku.productId)
            }
        }

        MaterialDialog.Builder(this)
                .title(R.string.donate)
                .adapter(DonationAdapter(this, this::onDonate), LinearLayoutManager(this))
                .dismissListener {
                    finish()
                    overridePendingTransition(0, 0)
                }
                .show()
    }

    fun onDonate(position: Int) {
        val item = donations[position]
        if (position < 4) {
            bp.purchase(this, item)
        } else {
            bp.subscribe(this, item)
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        bp.consumePurchase(productId)

        SettingsManager.donated = true

        AlertDialog.Builder(this)
                .setMessage(getString(R.string.donation_thank))
                .setNeutralButton(android.R.string.ok, null)
                .setCancelable(false)
                .create()
                .show()
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        /*
         * Called when some error occurred. See Constants class for more details
         */
        error?.printStackTrace()
    }

    override fun onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    companion object {

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        val donations = ArrayList(listOf("donation_2", "donation_5", "donation_10", "donation_20"))
        val prices: HashMap<String, String> = HashMap()
    }
}
