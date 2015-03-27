
package com.michaelflisar.licenses.licenses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class License implements Parcelable
{
    private String mName;
    private String mText;

    public License(Parcel parcel) {
        mName = parcel.readString();
        mText = parcel.readString();
    }

    public License(String name, int resId, Context ctx) {
        mName = name;
        mText = readRawTextFile(ctx, resId);
    }

    public String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            return null;
        }
        return byteArrayOutputStream.toString();
    }

    public String getName() {
        return mName;
    }
    
    public String getText() {
        return mText;
    }
    
    public void setText(String text) {
        mText = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mText);
    }
}
