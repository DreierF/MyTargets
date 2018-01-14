/*
 * Copyright (C) 2018 Florian Dreier
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
import android.net.Uri
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.BuildConfig
import de.dreier.mytargets.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

class AboutFragment : Fragment() {

    private val version: String
        get() = getString(R.string.version,
                BuildConfig.VERSION_NAME) + " (${BuildConfig.VERSION_CODE})"

    private val crowdinElement: Element
        get() = WebElement(R.string.translate_crowdin, R.drawable.about_icon_crowdin,
                URL_CROWDIN)

    private val betaTesterElement: Element
        get() = WebElement(R.string.test_beta, R.drawable.about_icon_beta_test, URL_GOOGLE_PLUS)

    private val googlePlusElement: Element
        get() = WebElement(R.string.join_on_google_plus, R.drawable.about_icon_google_plus,
                URL_GOOGLE_PLUS)

    private val payPalDonateElement: Element
        get() = WebElement(R.string.donate_via_paypal, R.drawable.about_icon_paypal, URL_PAYPAL)

    private val linkedInItem: Element
        get() = WebElement(R.string.network_linkedin, R.drawable.about_icon_linkedin,
                URL_LINKEDIN)

    private val shareElement: Element
        get() {
            val shareElement = Element(getString(R.string.share_with_friends), R.drawable.about_icon_share)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, URL_PLAY_STORE)
            sendIntent.type = "text/plain"
            shareElement.intent = Intent.createChooser(sendIntent, getString(R.string.share_with_friends))
            return shareElement
        }

    private val donateElement: Element
        get() {
            val donateElement = Element(getString(R.string.donate), R.drawable.about_icon_donate)
            donateElement.intent = Intent(context, DonateActivity::class.java)
            return donateElement
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return AboutPage(context)
                .isRTL(false)
                .setImage(R.drawable.product_logo_144dp)
                .setDescription(getString(R.string.my_targets) + "\n" + version)
                .addGroup(getString(R.string.contribute))
                .addItem(shareElement)
                .addItem(crowdinElement)
                .addItem(betaTesterElement)
                .addItem(donateElement)
                .addItem(payPalDonateElement)
                .addGroup(getString(R.string.connect))
                .addEmail("dreier.florian@gmail.com")
                .addPlayStore("de.dreier.mytargets")
                .addItem(googlePlusElement)
                .addGitHub("DreierF")
                .addItem(linkedInItem)
                .addGroup(getString(R.string.special_thanks_to))
                .addItem(Element(getString(R.string.all_beta_testers), null))
                .addItem(Element(getString(R.string.all_translators)
                        + "\n" + getString(R.string.translators), null))
                .create()
    }

    private inner class WebElement internal constructor(@StringRes title: Int, icon: Int?, url: String) : Element(getString(title), icon) {
        init {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(url)
            setIntent(intent)
        }
    }

    companion object {
        private const val URL_GOOGLE_PLUS = "https://plus.google.com/u/0/communities/102686119334423317437"
        private const val URL_PLAY_STORE = "http://play.google.com/store/apps/details?id=de.dreier.mytargets"
        private const val URL_PAYPAL = "https://www.paypal.me/floriandreier"
        private const val URL_CROWDIN = "https://crowdin.com/project/mytargets"
        private const val URL_LINKEDIN = "https://de.linkedin.com/in/florian-dreier-b056a1113"
    }
}
