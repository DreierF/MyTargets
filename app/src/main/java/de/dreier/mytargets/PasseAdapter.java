package de.dreier.mytargets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Shows all passes of one round
 */
public class PasseAdapter extends CursorAdapter {

    private final LayoutInflater mInflater;
    private final TargetOpenHelper db;
    private final int passeIdInd;
    private final TargetOpenHelper.Round roundInfo;

    public PasseAdapter(Context context, long round) {
        super(context,new TargetOpenHelper(context).getPasses(round),0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = new TargetOpenHelper(context);
        roundInfo = db.getRound(round);
        passeIdInd = getCursor().getColumnIndex(TargetOpenHelper.PASSE_ID);
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
            text.setText("Neue Passe");
        } else {
            view = super.getView(pos-1,convertView,parent);
        }
        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.passe_card, viewGroup, false);
        holder.shots = (PassenView) v.findViewById(R.id.shoots);
        holder.subtitle = (TextView) v.findViewById(R.id.passe);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.subtitle.setText("Passe "+(1+cursor.getPosition()));
        int[] points = db.getPasse(cursor.getLong(passeIdInd));
        holder.shots.setPoints(points,roundInfo.target);
    }

    public static class ViewHolder {
        public PassenView shots;
        public TextView subtitle;
    }
}
