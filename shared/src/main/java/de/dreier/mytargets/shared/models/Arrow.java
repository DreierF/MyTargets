package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class Arrow extends ImageHolder implements DatabaseSerializable {
    static final long serialVersionUID = 50L;
    public static final String TABLE = "ARROW";
    public static final String NAME = "name";
    public static final String THUMBNAIL = "thumbnail";
    public static final String LENGTH = "length";
    public static final String MATERIAL = "material";
    public static final String SPINE = "spine";
    public static final String WEIGHT = "weight";
    public static final String VANES = "vanes";
    public static final String NOCK = "nock";
    public static final String COMMENT = "comment";
    public static final String IMAGE = "image";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT," +
                    LENGTH + " TEXT," +
                    MATERIAL + " TEXT," +
                    SPINE + " TEXT," +
                    WEIGHT + " TEXT," +
                    VANES + " TEXT," +
                    NOCK + " TEXT," +
                    COMMENT + " TEXT," +
                    THUMBNAIL + " BLOB," +
                    IMAGE + " TEXT);";

    public String name;
    public String length;
    public String material;
    public String spine;
    public String weight;
    public String vanes;
    public String nock;
    public String comment;
    public List<Integer> numbers = new ArrayList<>();

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Arrow.NAME, name);
        values.put(Arrow.LENGTH, length);
        values.put(Arrow.MATERIAL, material);
        values.put(Arrow.SPINE, spine);
        values.put(Arrow.WEIGHT, weight);
        values.put(Arrow.VANES, vanes);
        values.put(Arrow.NOCK, nock);
        values.put(Arrow.COMMENT, comment);
        values.put(Arrow.THUMBNAIL, thumb);
        values.put(Arrow.IMAGE, imageFile);
        return values;
    }

    @Override
    public void fromCursor(Context context, Cursor cursor, int startColumnIndex) {
        setId(cursor.getLong(startColumnIndex));
        name = cursor.getString(startColumnIndex + 1);
        length = cursor.getString(startColumnIndex + 2);
        material = cursor.getString(startColumnIndex + 3);
        spine = cursor.getString(startColumnIndex + 4);
        weight = cursor.getString(startColumnIndex + 5);
        vanes = cursor.getString(startColumnIndex + 6);
        nock = cursor.getString(startColumnIndex + 7);
        comment = cursor.getString(startColumnIndex + 8);
        thumb = cursor.getBlob(startColumnIndex + 9);
        imageFile = cursor.getString(startColumnIndex + 10);
    }
}