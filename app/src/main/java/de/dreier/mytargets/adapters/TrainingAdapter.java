package de.dreier.mytargets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;

/**
 * Shows all Trainings
 */

public class TrainingAdapter extends NowListAdapter {

    private final int titleInd;
    private final int dateInd;
    private final DateFormat dateFormat;

    public TrainingAdapter(Context context) {
        super(context, new DatabaseManager(context).getTrainings());
        titleInd = getCursor().getColumnIndex(DatabaseManager.TRAINING_TITLE);
        dateInd = getCursor().getColumnIndex(DatabaseManager.TRAINING_DATE);
        dateFormat = DateFormat.getDateInstance();
        mNewText = context.getString(R.string.new_training);
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
}
