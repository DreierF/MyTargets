package de.dreier.mytargets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Target;

/**
 * Shows all rounds of one settings_only
 */
public class RoundsAdapter extends CursorRecyclerViewAdapter<RoundsAdapter.ViewHolder> {

    final DatabaseManager db;
    final Context mContext;
    private final int unitInd;
    private final int pppInd;
    private final int targetInd, idInd;
    private final int distInd;
    private final int indoorInd;

    public RoundsAdapter(Context context, long training) {
        super(DatabaseManager.getInstance(context).getRounds(training));
        db = DatabaseManager.getInstance(context);
        this.mContext = context;
        idInd = getCursor().getColumnIndex(DatabaseManager.ROUND_ID);
        distInd = getCursor().getColumnIndex(DatabaseManager.ROUND_DISTANCE);
        unitInd = getCursor().getColumnIndex(DatabaseManager.ROUND_UNIT);
        indoorInd = getCursor().getColumnIndex(DatabaseManager.ROUND_INDOOR);
        pppInd = getCursor().getColumnIndex(DatabaseManager.ROUND_PPP);
        targetInd = getCursor().getColumnIndex(DatabaseManager.ROUND_TARGET);
    }

    @Override
    public ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.round_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        int tar = cursor.getInt(targetInd);
        long round = cursor.getLong(idInd);
        int max = Target.getMaxPoints(tar);
        int reached = db.getRoundPoints(round);
        int maxP = cursor.getInt(pppInd) * max * db.getPasses(round).getCount();

        String title = mContext.getString(R.string.round) + " " + (1 + cursor.getPosition());
        String subtitle = cursor.getInt(distInd) + cursor.getString(unitInd) + " - " + mContext.getString(cursor.getInt(indoorInd) == 0 ? R.string.outdoor : R.string.indoor);
        String annotation = reached + "/" + maxP;
        viewHolder.bindCursor(title, subtitle, annotation);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mSubtitle;
        public TextView mGes;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.round);
            mSubtitle = (TextView) itemView.findViewById(R.id.dist);
            mGes = (TextView) itemView.findViewById(R.id.gesRound);
        }

        public void bindCursor(String title, String subtitle, String annotation) {
            mTitle.setText(title);
            mSubtitle.setText(subtitle);
            mGes.setText(annotation);
        }
    }
}
