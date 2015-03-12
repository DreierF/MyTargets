package de.dreier.mytargets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Training;

/**
 * Shows all Trainings
 */

public class TrainingAdapter extends ArrayAdapter<Training> {

    final LayoutInflater mInflater;
    private final DateFormat dateFormat;

    public TrainingAdapter(Context context) {
        super(context, R.layout.training_card, DatabaseManager.getInstance(context).getTrainings());
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dateFormat = DateFormat.getDateInstance();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            ViewHolder holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.training_card, viewGroup, false);
            holder.title = (TextView) convertView.findViewById(R.id.training);
            holder.subtitle = (TextView) convertView.findViewById(R.id.training_date);
            holder.ges = (TextView) convertView.findViewById(R.id.gesTraining);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        Training t = getItem(pos);
        holder.title.setText(t.title);
        holder.subtitle.setText(dateFormat.format(t.date));
        holder.ges.setText(t.reachedPoints + "/" + t.maxPoints);
        return convertView;
    }

    public static class ViewHolder {
        public TextView title;
        public TextView subtitle;
        public TextView ges;
    }
}
