package de.dreier.mytargets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.TargetOpenHelper;

/**
 * Shows all Trainings
 */

public class TrainingAdapter extends NowListAdapter {

    private final int titleInd;
    private final int dateInd;
    private final DateFormat dateFormat;

    public TrainingAdapter(Context context) {
        super(context, new TargetOpenHelper(context).getTrainings());
        titleInd = getCursor().getColumnIndex(TargetOpenHelper.TRAINING_TITLE);
        dateInd = getCursor().getColumnIndex(TargetOpenHelper.TRAINING_DATE);
        dateFormat = DateFormat.getDateInstance();
        mNewText = context.getString(R.string.new_training);
        mExtraCards = 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.training_card, viewGroup, false);
        holder.title = (TextView) v.findViewById(R.id.training);
        holder.subtitle = (TextView) v.findViewById(R.id.training_date);
        holder.ges = (TextView) v.findViewById(R.id.gesTraining);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(cursor.getString(titleInd));
        holder.subtitle.setText(dateFormat.format(new Date(cursor.getLong(dateInd))));
        int[] points = db.getTrainingPoints(cursor.getLong(0));
        holder.ges.setText(points[0] + "/" + points[1]);
    }

    public static class ViewHolder {
        public TextView title;
        public TextView subtitle;
        public TextView ges;
    }

    @Override
    protected View buildExtraCard(int pos, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.mybows_card, parent, false);
        } else {
            view = convertView;
        }
        return view;
    }
}
