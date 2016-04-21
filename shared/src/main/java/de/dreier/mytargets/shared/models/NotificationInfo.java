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

import de.dreier.mytargets.shared.utils.ParcelableUtil;

@Parcel
public class NotificationInfo implements Parcelable {
    public static final Creator<NotificationInfo> CREATOR
            = new ParcelableUtil.Creator<>(NotificationInfo.class);
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
        parcel.writeParcelable(Parcels.wrap(this), flags);
    }
}
