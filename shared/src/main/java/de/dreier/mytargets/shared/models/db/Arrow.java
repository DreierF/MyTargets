package de.dreier.mytargets.shared.models.db;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IImageProvider;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.utils.DimensionConverter;
import de.dreier.mytargets.shared.utils.ThumbnailConverter;

@Parcel
@Table(database = AppDatabase.class, name = "ARROW")
public class Arrow extends BaseModel implements IImageProvider, IIdSettable, Comparable<Arrow> {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public Long id = -1L;

    @Column(name = "name")
    public String name = "";

    @Column(name = "length")
    public String length = "";

    @Column(name = "material")
    public String material = "";

    @Column(name = "spine")
    public String spine = "";

    @Column(name = "weight")
    public String weight = "";

    @Column(name = "tip_weight")
    public String tipWeight = "";

    @Column(name = "vanes")
    public String vanes = "";

    @Column(name = "nock")
    public String nock = "";

    @Column(name = "comment")
    public String comment = "";

    @Column(typeConverter = DimensionConverter.class, name = "diameter")
    public Dimension diameter = new Dimension(5, Dimension.Unit.MILLIMETER);

    @Column(name = "thumbnail", typeConverter = ThumbnailConverter.class)
    public Thumbnail thumbnail;

    @Column(name = "image")
    public String imageFile;

    public static List<Arrow> getAll() {
        return SQLite.select().from(Arrow.class).queryList();
    }

    public static Arrow get(Long id) {
        return SQLite.select()
                .from(Arrow.class)
                .where(Arrow_Table._id.eq(id))
                .querySingle();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return another instanceof Arrow &&
                getClass().equals(another.getClass()) &&
                id.equals(((Arrow) another).id);
    }

    @Override
    public int compareTo(@NonNull Arrow another) {
        final int result = getName().compareTo(another.getName());
        return result == 0 ? (int) (id - another.id) : result;
    }

    public static void deleteAll() {
        SQLite.delete(Arrow.class).execute();
    }
}
