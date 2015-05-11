package com.michaelflisar.licenses.licenses;

import android.content.Context;

import com.michaelflisar.licensesdialog.R;

public class Licenses {
    public static License LICENSE_APACHE_V2;
    public static License CC_BY_ND_V3;
    
    public static void init(Context cxt) {
    	LICENSE_APACHE_V2 = new License("Apache License V2.0", R.raw.apache_license_2, cxt);
        CC_BY_ND_V3 = new License("CC BY-ND 3.0", R.raw.cc_by_nd, cxt);
    }
    
    public static LicenseEntry createLicense(String name, String version, String author, String copyright) {
        return new LicenseEntry(name, version, author, LICENSE_APACHE_V2);
    }

    public static LicenseEntry createLicenseIcon8() {
        return new LicenseEntry("icons8.com", "", "icons8.com", CC_BY_ND_V3);
    }
}
