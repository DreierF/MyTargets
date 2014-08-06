package de.dreier.mytargets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Shows all rounds of one training
 */
public class RundenAdapter extends CursorAdapter {

    private final LayoutInflater mInflater;

    public RundenAdapter(Context context, long training) {
        super(context,new TargetOpenHelper(context).getRunden(training),0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 1+super.getCount();
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
            text.setText("Neues Runde");
        } else {
            view = super.getView(pos-1,convertView,parent);
        }
        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.round_card, viewGroup, false);
        holder.title = (TextView) v.findViewById(R.id.round);
        holder.subtitle = (TextView) v.findViewById(R.id.dist);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText("Runde "+(1+cursor.getPosition()));
        holder.subtitle.setText("30m");
    }

    public static class ViewHolder {
        public TextView title;
        public TextView subtitle;
    }
}
