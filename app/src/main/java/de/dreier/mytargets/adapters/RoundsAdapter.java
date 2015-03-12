package de.dreier.mytargets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Target;

/**
 * Shows all rounds of one settings_only
 */
public class RoundsAdapter extends CursorAdapter {

    final LayoutInflater mInflater;
    final DatabaseManager db;
    final Context mContext;
    private final int unitInd;
    private final int pppInd;
    private final int targetInd, idInd;
    private final int distInd;
    private final int indoorInd;
    String mNewText;

    public RoundsAdapter(Context context, long training) {
        super(context, DatabaseManager.getInstance(context).getRounds(training), 0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = DatabaseManager.getInstance(context);
        mContext = context;
        idInd = getCursor().getColumnIndex(DatabaseManager.ROUND_ID);
        distInd = getCursor().getColumnIndex(DatabaseManager.ROUND_DISTANCE);
        unitInd = getCursor().getColumnIndex(DatabaseManager.ROUND_UNIT);
        indoorInd = getCursor().getColumnIndex(DatabaseManager.ROUND_INDOOR);
        pppInd = getCursor().getColumnIndex(DatabaseManager.ROUND_PPP);
        targetInd = getCursor().getColumnIndex(DatabaseManager.ROUND_TARGET);
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
        holder.subtitle.setText(cursor.getInt(distInd) + cursor.getString(unitInd) + " - " + context.getString(cursor.getInt(indoorInd) == 0 ? R.string.outdoor : R.string.indoor));
        holder.ges.setText(reached + "/" + maxP);
    }

    public static class ViewHolder {
        public TextView title;
        public TextView subtitle;
        public TextView ges;
    }
}
