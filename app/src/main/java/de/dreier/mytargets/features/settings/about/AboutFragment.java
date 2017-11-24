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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.BuildConfig;
import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.IntentWrapper;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {
    private static final String URL_GOOGLE_PLUS = "https://plus.google.com/u/0/communities/102686119334423317437";
    private static final String URL_PLAY_STORE = "http://play.google.com/store/apps/details?id=de.dreier.mytargets";
    private static final String URL_PAYPAL = "https://www.paypal.me/floriandreier";
    private static final String URL_CROWDIN = "https://crowdin.com/project/mytargets";
    private static final String URL_LINKEDIN = "https://de.linkedin.com/in/florian-dreier-b056a1113";

    public static IntentWrapper getIntent() {
        return new IntentWrapper(AboutActivity.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new AboutPage(getContext())
                .isRTL(false)
                .setImage(R.drawable.product_logo_144dp)
                .setDescription(getString(R.string.my_targets) + "\n" + getVersion())
                .addGroup(getString(R.string.contribute))
                .addItem(getShareElement())
                .addItem(getCrowdinElement())
                .addItem(getBetaTesterElement())
                .addItem(getDonateElement())
                .addItem(getPayPalDonateElement())
                .addGroup(getString(R.string.connect))
                .addEmail("dreier.florian@gmail.com")
                .addPlayStore("de.dreier.mytargets")
                .addItem(getGooglePlusElement())
                .addGitHub("DreierF")
                .addItem(getLinkedInItem())
                .addGroup(getString(R.string.special_thanks_to))
                .addItem(new Element(getString(R.string.all_beta_testers), null))
                .addItem(new Element(getString(R.string.all_translators)
                        + "\n" + getString(R.string.translators), null))
                .create();
    }

    @NonNull
    private String getVersion() {
        return getString(R.string.version,
                BuildConfig.VERSION_NAME) + " (" + BuildConfig.VERSION_CODE + ")";
    }

    private Element getCrowdinElement() {
        return new WebElement(R.string.translate_crowdin, R.drawable.about_icon_crowdin,
                URL_CROWDIN);
    }

    private Element getBetaTesterElement() {
        return new WebElement(R.string.test_beta, R.drawable.about_icon_beta_test, URL_GOOGLE_PLUS);
    }

    private Element getGooglePlusElement() {
        return new WebElement(R.string.join_on_google_plus, R.drawable.about_icon_google_plus,
                URL_GOOGLE_PLUS);
    }

    private Element getPayPalDonateElement() {
        return new WebElement(R.string.donate_via_paypal, R.drawable.about_icon_paypal, URL_PAYPAL);
    }

    private Element getLinkedInItem() {
        return new WebElement(R.string.network_linkedin, R.drawable.about_icon_linkedin,
                URL_LINKEDIN);
    }

    @NonNull
    private Element getShareElement() {
        Element shareElement = new Element(getString(R.string.share_with_friends), R.drawable.about_icon_share);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, URL_PLAY_STORE);
        sendIntent.setType("text/plain");
        shareElement.setIntent(
                Intent.createChooser(sendIntent, getString(R.string.share_with_friends)));
        return shareElement;
    }

    @NonNull
    private Element getDonateElement() {
        Element donateElement = new Element(getString(R.string.donate), R.drawable.about_icon_donate);
        donateElement.setIntent(new Intent(getContext(), DonateActivity.class));
        return donateElement;
    }

    private class WebElement extends Element {
        WebElement(@StringRes int title, Integer icon, String url) {
            super(getString(title), icon);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            setIntent(intent);
        }
    }
}
