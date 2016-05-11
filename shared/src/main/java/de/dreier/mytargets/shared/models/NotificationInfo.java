/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.os.Parcelable;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.lang.reflect.Array;

@Parcel
public class NotificationInfo implements Parcelable {
    public static final Creator<NotificationInfo> CREATOR
            = new Parcelable.Creator<NotificationInfo>() {

    public NotificationInfo createFromParcel(android.os.Parcel parcel) {
        String title = parcel.readString();
        String text = parcel.readString();
        Round round = parcel.readParcelable(Round$$Parcelable.class.getClassLoader());
        return new NotificationInfo(round, title, text);
    }

    @Override public NotificationInfo[] newArray(int i) {
        return (NotificationInfo[]) Array.newInstance(NotificationInfo.class, i);
    }
};
    public String title;
    public String text;
    public Round round;

    public NotificationInfo() {
    }
    public NotificationInfo(Round round, String title, String text) {
        this.round = round;
        this.title = title;
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeParcelable(Parcels.wrap(round), flags);
    }
}
