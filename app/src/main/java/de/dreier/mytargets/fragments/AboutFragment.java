package de.dreier.mytargets.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.MainActivity;
import de.dreier.mytargets.utils.BackupUtils;
import de.dreier.mytargets.utils.IABHelperWrapper;
import de.dreier.mytargets.utils.Utils;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment implements DonateDialogFragment.DonationListener {
    private static final String DONATE = "donate";

    private IABHelperWrapper mIABWrapper;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIABWrapper.showDialog(AboutFragment.this);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DONATE);
        getContext().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(receiver);
        mIABWrapper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mIABWrapper.handleActivityResult(requestCode, resultCode, data)) {
            if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
                final Uri uri = data.getData();
                if (BackupUtils.Import(getActivity(), uri)) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDonate(int position) {
        mIABWrapper.startDonationForItem(position);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIABWrapper = new IABHelperWrapper((AppCompatActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new AboutPage(getContext())
                .isRTL(false)
                .setImage(R.drawable.logo)
                .setDescription(getString(R.string.my_targets) + "\n" + getVersion())
                .addGroup("Contribute")
                .addItem(getShareElement())
                .addItem(getCrowdinElement())
                .addItem(getDonateElement())
                .addItem(getPayPalDonateElement())
                .addGroup("Connect")
                .addEmail("dreier.florian@gmail.com")
                .addPlayStore("de.dreier.mytargets")
                .addGitHub("DreierF")
                .addItem(getLinkedInItem())
                .addGroup("Special thanks to")
                .addItem(new Element("translators", "All guys, who helped with translating the app!", null))
                .create();
    }

    private Element getShareElement() {
        Element shareElement = new Element();
        shareElement.setIcon(R.drawable.about_icon_share);
        shareElement.setTitle("Share with your friends");
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "http://play.google.com/store/apps/details?id=de.dreier.mytargets");
        sendIntent.setType("text/plain");
        shareElement.setIntent(sendIntent);
        return shareElement;
    }

    private Element getDonateElement() {
        Element donateElement = new Element();
        donateElement.setIcon(R.drawable.about_icon_donate);
        donateElement.setTitle("Donate");
        donateElement.setIntent(new Intent(DONATE));
        return donateElement;
    }

    private Element getPayPalDonateElement() {
        Element payPalElement = new Element();
        payPalElement.setIcon(R.drawable.about_icon_paypal);
        payPalElement.setTitle("Donate via PayPal");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.paypal.me/floriandreier"));
        payPalElement.setIntent(intent);
        return payPalElement;
    }

    private Element getCrowdinElement() {
        Element crowdinElement = new Element();
        crowdinElement.setIcon(R.drawable.about_icon_crowdin);
        crowdinElement.setTitle("Translate on Crowdin");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://crowdin.com/project/mytargets"));
        crowdinElement.setIntent(intent);
        return crowdinElement;
    }

    private Element getLinkedInItem() {
        Element linkedInElement = new Element();
        linkedInElement.setIcon(R.drawable.about_icon_linkedin);
        linkedInElement.setTitle("Network on LinkedIn");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://de.linkedin.com/in/florian-dreier-b056a1113"));
        linkedInElement.setIntent(intent);
        return linkedInElement;
    }

    @NonNull
    private String getVersion() {
        PackageInfo appVersionInfo = Utils.getAppVersionInfo(getContext());
        if (appVersionInfo != null) {
            String versionName = appVersionInfo.versionName;
            return getString(R.string.version, versionName);
        } else {
            return getString(R.string.version, "unknown");
        }
    }

}
