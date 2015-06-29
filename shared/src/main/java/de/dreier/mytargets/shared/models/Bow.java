package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import de.dreier.mytargets.shared.utils.BitmapUtils;

public class Bow extends IdProvider implements DatabaseSerializable {
    static final long serialVersionUID = 52L;
    public static final String TABLE = "BOW";
    public static final String NAME = "name";
    public static final String THUMBNAIL = "thumbnail";
    public static final String BRAND = "brand";
    public static final String TYPE = "type";
    public static final String SIZE = "size";
    public static final String HEIGHT = "height";
    public static final String TILLER = "tiller";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String CREATE_TABLE_BOW =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT," +
                    BRAND + " TEXT," +
                    TYPE + " INTEGER," +
                    SIZE + " INTEGER," +
                    HEIGHT + " TEXT," +
                    TILLER + " TEXT," +
                    DESCRIPTION + " TEXT," +
                    THUMBNAIL + " BLOB," +
                    IMAGE + " TEXT);";

    public String imageFile;
    public String name;
    public int type;
    public String brand;
    public String size;
    public String height;
    public String tiller;
    public String description;
    public Bitmap image;

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Bow.NAME, name);
        values.put(Bow.TYPE, type);
        values.put(Bow.BRAND, brand);
        values.put(Bow.SIZE, size);
        values.put(Bow.HEIGHT, height);
        values.put(Bow.TILLER, tiller);
        values.put(Bow.DESCRIPTION, description);
        Bitmap thumb = ThumbnailUtils.extractThumbnail(image, 100, 100);
        byte[] imageData = BitmapUtils.getBitmapAsByteArray(thumb);
        values.put(Bow.THUMBNAIL, imageData);
        values.put(Bow.IMAGE, imageFile);
        return values;
    }

    @Override
    public void fromCursor(Cursor cursor, int startColumnIndex) {
        setId(cursor.getLong(0));
        name = cursor.getString(1);
        type = cursor.getInt(2);
        brand = cursor.getString(3);
        size = cursor.getString(4);
        height = cursor.getString(5);
        tiller = cursor.getString(6);
        description = cursor.getString(7);
    }
}
