package de.dreier.mytargets;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Shows all Trainings
 * */

public class BowAdapter extends NowListAdapter {

    private final int nameInd;

    public BowAdapter(Context context) {
        super(context,new TargetOpenHelper(context).getTrainings());
        nameInd = getCursor().getColumnIndex(TargetOpenHelper.BOW_NAME);
        mNewText = context.getString(R.string.new_bow);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.bow_card, viewGroup, false);
        holder.name = (TextView) v.findViewById(R.id.name);
        holder.img = (ImageView) v.findViewById(R.id.bowImage);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(cursor.getString(nameInd));
        holder.img.setImageResource(R.drawable.ic_launcher);
    }

    public static class ViewHolder {
        public ImageView img;
        public TextView name;
        public TextView subtitle;
    }
}
