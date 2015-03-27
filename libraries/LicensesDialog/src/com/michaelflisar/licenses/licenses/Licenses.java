package com.michaelflisar.licenses.licenses;

import com.michaelflisar.licensesdialog.R;

import android.content.Context;

public class Licenses {
    public static License LICENSE_APACHE_V2;
    
    public static void init(Context cxt) {
    	LICENSE_APACHE_V2 = new License("Apache License V2.0", R.raw.apache_license_2, cxt);
    }
    
    public static LicenseEntry createLicense(String name, String version, String author, String copyright) {
        return new LicenseEntry(name, version, author, LICENSE_APACHE_V2);
    }
}
