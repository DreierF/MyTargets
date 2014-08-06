package de.dreier.mytargets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Shows all Trainings
 * */

public class TrainingAdapter extends CursorAdapter {

    private final LayoutInflater mInflater;
    private final int titleInd;
    private final int dateInd;
    private final DateFormat dateFormat;

    public TrainingAdapter(Context context) {
        super(context,new TargetOpenHelper(context).getTrainings(),0);
        titleInd = getCursor().getColumnIndex(TargetOpenHelper.TRAINING_TITLE);
        dateInd = getCursor().getColumnIndex(TargetOpenHelper.TRAINING_DATE);
        dateFormat = DateFormat.getDateInstance();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 1+getCursor().getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position==0?0:1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int pos) {
        return pos==0?0:super.getItemId(pos-1);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View view;
        if(pos==0) {
            if (convertView == null) {
                view = mInflater.inflate(R.layout.new_card, parent, false);
            } else {
                view = convertView;
            }
            TextView text = (TextView) view.findViewById(R.id.newText);
            text.setText("Neues Training");
        } else {
            view = super.getView(pos-1,convertView,parent);
        }
        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.training_card, viewGroup, false);
        holder.title = (TextView) v.findViewById(R.id.training);
        holder.subtitle = (TextView) v.findViewById(R.id.training_date);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(cursor.getString(titleInd));
        holder.subtitle.setText(dateFormat.format(new Date(cursor.getLong(dateInd))));
    }

    public static class ViewHolder {
        public TextView title;
        public TextView subtitle;
    }
}
