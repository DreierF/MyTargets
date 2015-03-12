package de.dreier.mytargets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;

/**
 * Shows all Trainings
 */

public class BowAdapter extends CursorRecyclerViewAdapter<BowAdapter.ViewHolder> {

    private final int nameInd, thumbInd;

    public BowAdapter(Context context) {
        super(DatabaseManager.getInstance(context).getArrows());
        nameInd = getCursor().getColumnIndex(DatabaseManager.ARROW_NAME);
        thumbInd = getCursor().getColumnIndex(DatabaseManager.ARROW_THUMBNAIL);
    }

    @Override
    public ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bow_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        byte[] data = cursor.getBlob(thumbInd);
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        viewHolder.bindCursor(cursor.getString(nameInd), image);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImg;
        public TextView mName;
        public TextView mSubtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mImg = (ImageView) itemView.findViewById(R.id.image);
        }

        public void bindCursor(String name, Bitmap bmp) {
            mName.setText(name);
            mImg.setImageBitmap(bmp);
        }
    }
}
