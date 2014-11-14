package de.dreier.mytargets;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Shows all rounds of one settings_only
 */
public class RoundsAdapter extends NowListAdapter {

    private final int unitInd;
    private final int pppInd;
    private final int targetInd, idInd;
    private int distInd, indoorInd;

    public RoundsAdapter(Context context, long training) {
        super(context, new TargetOpenHelper(context).getRunden(training));
        idInd = getCursor().getColumnIndex(TargetOpenHelper.RUNDE_ID);
        distInd = getCursor().getColumnIndex(TargetOpenHelper.RUNDE_DISTANCE);
        unitInd = getCursor().getColumnIndex(TargetOpenHelper.RUNDE_UNIT);
        indoorInd = getCursor().getColumnIndex(TargetOpenHelper.RUNDE_INDOOR);
        pppInd = getCursor().getColumnIndex(TargetOpenHelper.RUNDE_PPP);
        targetInd = getCursor().getColumnIndex(TargetOpenHelper.RUNDE_TARGET);
        mNewText = context.getString(R.string.new_round);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.round_card, viewGroup, false);
        holder.title = (TextView) v.findViewById(R.id.round);
        holder.subtitle = (TextView) v.findViewById(R.id.dist);
        holder.ges = (TextView) v.findViewById(R.id.gesRound);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int tar = cursor.getInt(targetInd);
        long round = cursor.getLong(idInd);
        int max = Target.getMaxPoints(tar);
        int reached = db.getRoundPoints(round);
        int maxP = cursor.getInt(pppInd) * max * db.getPasses(round).getCount();

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(context.getString(R.string.round) + " " + (1 + cursor.getPosition()));
        holder.subtitle.setText(NewRoundActivity.distanceValues[cursor.getInt(distInd)] + cursor.getString(unitInd) + " - " + context.getString(cursor.getInt(indoorInd) == 0 ? R.string.outdoor : R.string.indoor));
        holder.ges.setText(reached + "/" + maxP);
    }

    public static class ViewHolder {
        public TextView title;
        public TextView subtitle;
        public TextView ges;
    }
}
