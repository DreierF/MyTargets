
package com.michaelflisar.licenses.licenses;

import android.os.Parcel;
import android.os.Parcelable;

public class LicenseEntry implements Parcelable {
    protected String libraryName;
    protected String libraryVersion;
    protected String libraryAuthor;
    protected String libraryLink;
    
    protected License license;
    
    public LicenseEntry() {
        libraryName = null;
        libraryVersion = null;
        libraryAuthor = null;
        libraryLink = null;
        
        license = null;
    }

    public LicenseEntry(Parcel parcel) {
        libraryName = parcel.readString();
        libraryVersion = parcel.readString();
        libraryAuthor = parcel.readString();
        libraryLink = parcel.readString();
        
        license = parcel.readParcelable(null);
    }
    
    public LicenseEntry(String libraryName, String libraryVersion, String libraryAuthor, License license) {
        this.libraryName = libraryName;
        this.libraryVersion = libraryVersion;
        this.libraryAuthor = libraryAuthor;
        this.libraryLink = "https://github.com/" + libraryAuthor+"/" + libraryName;
        this.license = license;
    }
    
    public License getLicense() {
        return license;
    }
    
    public String getLibraryName() {
        return libraryName;
    }

    public String getLibraryVersion() {
        return libraryVersion;
    }

    public String getLibraryAuthor() {
        return libraryAuthor;
    }

    public String getLibraryLink() {
        return libraryLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(libraryName);
        dest.writeString(libraryVersion);
        dest.writeString(libraryAuthor);
        dest.writeString(libraryLink);
        dest.writeParcelable(license, 0);
    }
}
