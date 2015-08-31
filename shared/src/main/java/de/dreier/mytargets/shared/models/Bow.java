package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import de.dreier.mytargets.shared.R;

public class Bow extends ImageHolder implements DatabaseSerializable {
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
    public static final String LIMBS = "limbs";
    public static final String SIGHT = "sight";
    public static final String WEIGHT = "draw_weight";
    public static final String CREATE_TABLE_BOW =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT," +
                    BRAND + " TEXT," +
                    TYPE + " INTEGER," +
                    SIZE + " INTEGER," +
                    HEIGHT + " TEXT," +
                    TILLER + " TEXT," +
                    LIMBS + " TEXT," +
                    SIGHT + " TEXT," +
                    WEIGHT + " TEXT," +
                    DESCRIPTION + " TEXT," +
                    THUMBNAIL + " BLOB," +
                    IMAGE + " TEXT);";

    public String name;
    public int type;
    public String brand;
    public String size;
    public String height;
    public String tiller;
    public String limbs;
    public String sight;
    public String drawWeight;
    public String description;

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public Bitmap getImage(Context context) {
        Bitmap img = super.getImage(context);
        if (img == null) {
            context.getResources().getDrawable(R.drawable.recurve_bow, null);
        }
        return img;
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
        values.put(Bow.LIMBS, limbs);
        values.put(Bow.SIGHT, sight);
        values.put(Bow.WEIGHT, drawWeight);
        values.put(Bow.DESCRIPTION, description);
        values.put(Bow.THUMBNAIL, thumb);
        values.put(Bow.IMAGE, imageFile);
        return values;
    }

    @Override
    public void fromCursor(Context context, Cursor cursor, int startColumnIndex) {
        setId(cursor.getLong(startColumnIndex));
        name = cursor.getString(startColumnIndex + 1);
        type = cursor.getInt(startColumnIndex + 2);
        brand = cursor.getString(startColumnIndex + 3);
        size = cursor.getString(startColumnIndex + 4);
        height = cursor.getString(startColumnIndex + 5);
        tiller = cursor.getString(startColumnIndex + 6);
        limbs = cursor.getString(startColumnIndex + 7);
        sight = cursor.getString(startColumnIndex + 8);
        drawWeight = cursor.getString(startColumnIndex + 9);
        description = cursor.getString(startColumnIndex + 10);
        thumb = cursor.getBlob(startColumnIndex + 11);
        imageFile = cursor.getString(startColumnIndex + 12);
    }
}
