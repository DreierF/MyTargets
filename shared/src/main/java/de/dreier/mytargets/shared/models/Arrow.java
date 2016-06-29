package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import de.dreier.mytargets.shared.models.ArrowNumber_Table;

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
import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable;
import de.dreier.mytargets.shared.utils.ThumbnailConverter;

@Parcel
@Table(database = AppDatabase.class, name = Arrow.TABLE)
public class Arrow extends BaseModel implements IImageProvider, IIdSettable {
    public static final String TABLE = "ARROW";
    private static final String NAME = "name";
    private static final String THUMBNAIL = "thumbnail";
    private static final String LENGTH = "length";
    private static final String MATERIAL = "material";
    private static final String SPINE = "spine";
    private static final String WEIGHT = "weight";
    private static final String TIP_WEIGHT = "tip_weight";
    private static final String VANES = "vanes";
    private static final String NOCK = "nock";
    private static final String COMMENT = "comment";
    private static final String IMAGE = "image";

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public Long id = -1L;

    @Column(name = NAME)
    public String name = "";

    @Column(name = LENGTH)
    public String length = "";

    @Column(name = MATERIAL)
    public String material = "";

    @Column(name = SPINE)
    public String spine = "";

    @Column(name = WEIGHT)
    public String weight = "";

    @Column(name = TIP_WEIGHT)
    public String tipWeight = "";

    @Column(name = VANES)
    public String vanes = "";

    @Column(name = NOCK)
    public String nock = "";

    @Column(name = COMMENT)
    public String comment = "";

    @Column(name = THUMBNAIL, typeConverter = ThumbnailConverter.class)
    public Thumbnail thumbnail;

    @Column(name = IMAGE)
    public String imageFile;

    public List<ArrowNumber> numbers = new ArrayList<>();

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "numbers")
    public List<ArrowNumber> getArrowNumbers() {
        if (numbers == null || numbers.isEmpty()) {
            numbers = SQLite.select()
                    .from(ArrowNumber.class)
                    .where(ArrowNumber_Table.arrowId__id.eq(id))
                    .queryList();
        }
        return numbers;
    }

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
        return another instanceof Arrow &&
                getClass().equals(another.getClass()) &&
                id == ((Arrow) another).id;
    }
}
