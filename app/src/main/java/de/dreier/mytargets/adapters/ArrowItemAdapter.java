package de.dreier.mytargets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;

public class ArrowItemAdapter extends CursorAdapter {

    private final int nameInd, thumbInd;
    private final LayoutInflater mInflater;

    public ArrowItemAdapter(Context context) {
        super(context, DatabaseManager.getInstance(context).getArrows(), 0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        nameInd = getCursor().getColumnIndex(DatabaseManager.ARROW_NAME);
        thumbInd = getCursor().getColumnIndex(DatabaseManager.ARROW_THUMBNAIL);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.image_item, viewGroup, false);

        ViewHolder holder = new ViewHolder();
        holder.img = (ImageView) v.findViewById(R.id.image);
        holder.name = (TextView) v.findViewById(R.id.name);
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

    private class ViewHolder {
        ImageView img;
        TextView name;
    }
}