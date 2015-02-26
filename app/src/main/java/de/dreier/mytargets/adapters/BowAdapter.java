package de.dreier.mytargets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;

/**
 * Shows all Trainings
 */

public class BowAdapter extends NowListAdapter {

    private final int nameInd, thumbInd;

    public BowAdapter(Context context) {
        super(context, DatabaseManager.getInstance(context).getBows());
        nameInd = getCursor().getColumnIndex(DatabaseManager.BOW_NAME);
        thumbInd = getCursor().getColumnIndex(DatabaseManager.BOW_THUMBNAIL);
        mNewText = context.getString(R.string.new_bow);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.bow_card, viewGroup, false);
        holder.name = (TextView) v.findViewById(R.id.name);
        holder.img = (ImageView) v.findViewById(R.id.image);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(cursor.getString(nameInd));
        byte[] data = cursor.getBlob(thumbInd);
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        holder.img.setImageBitmap(image);
    }

    public static class ViewHolder {
        public ImageView img;
        public TextView name;
        public TextView subtitle;
    }
}
