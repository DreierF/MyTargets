package de.dreier.mytargets.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import de.dreier.mytargets.managers.SettingsManager;

/**
 * Created by florian on 29.05.16.
 */
public class TranslationUtils {
    private static boolean shownThisTime = false;

    public static void askForHelpTranslating(Context context) {
        ArrayList<String> supportedLanguages = new ArrayList<>();
        //TODO update
        Collections.addAll(supportedLanguages, "de", "en", "fr", "es", "ru", "nl", "it", "sl", "ca",
                "zh", "tr", "hu", "sl");
        boolean shown = SettingsManager.getTranslationDialogWasShown();
        String longLang = Locale.getDefault().getDisplayLanguage();
        String shortLocale = Locale.getDefault().getLanguage();
        if (!supportedLanguages.contains(shortLocale) && !shown && !shownThisTime) {
            // Link the e-mail address in the message
            final SpannableString s = new SpannableString(Html.fromHtml("If you would like " +
                    "to help make MyTargets even better by translating the app to " +
                    longLang +
                    " visit <a href=\"https://crowdin.com/project/mytargets\">crowdin</a>!<br /><br />" +
                    "Thanks in advance :)"));
            Linkify.addLinks(s, Linkify.WEB_URLS);
            AlertDialog d = new AlertDialog.Builder(context)
                    .setTitle("App translation")
                    .setMessage(s)
                    .setPositiveButton("OK", (dialog, which) -> {
                        SettingsManager.setTranslationDialogWasShown(true);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Remind me later", (dialog, which) -> {
                        shownThisTime = true;
                        dialog.dismiss();
                    }).create();
            d.show();
            ((TextView) d.findViewById(android.R.id.message))
                    .setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
