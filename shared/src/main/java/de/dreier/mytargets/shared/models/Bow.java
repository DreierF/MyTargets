package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.utils.ThumbnailConverter;

@Table(database = AppDatabase.class)
public class Bow extends BaseModel implements IImageProvider, IIdSettable {
    @PrimaryKey(autoincrement = true)
    public Long id = -1L;
    @Column
    public String name = "";
    @Column
    public EBowType type = EBowType.RECURVE_BOW;
    @Column
    public String brand = "";
    @Column
    public String size = "";
    @Column
    public String braceHeight = "";
    @Column
    public String tiller = "";
    @Column
    public String limbs = "";
    @Column
    public String sight = "";
    @Column
    public String drawWeight = "";
    @Column
    public String stabilizer = "";
    @Column
    public String clicker = "";
    @Column
    public String description = "";

    @Column(typeConverter = ThumbnailConverter.class)
    public Thumbnail thumbnail;
    @Column
    public String imageFile;

    public List<SightSetting> sightSettings = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Drawable getDrawable() {
        return thumbnail.getRoundDrawable();
    }

    @Override
    public Drawable getDrawable(Context context) {
        return getDrawable();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Bow &&
                getClass().equals(another.getClass()) &&
                id == ((Bow) another).id;
    }
}
