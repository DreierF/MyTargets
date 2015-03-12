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
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.views.PassesView;

/**
 * Shows all passes of one round
 */
public class PasseAdapter extends CursorRecyclerViewAdapter<PasseAdapter.ViewHolder> {

    final DatabaseManager db;
    final Context mContext;
    private final int passeIdInd;
    private final Round mRoundInfo;

    public PasseAdapter(Context context, long round, Round roundInfo) {
        super(DatabaseManager.getInstance(context).getPasses(round));
        db = DatabaseManager.getInstance(context);
        this.mContext = context;
        mRoundInfo = roundInfo;
        passeIdInd = getCursor().getColumnIndex(DatabaseManager.PASSE_ID);
    }

    @Override
    public ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.passe_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Shot[] points = db.getPasse(cursor.getLong(passeIdInd));
        viewHolder.bindCursor(mContext.getString(R.string.passe) + " " + (1 + cursor.getPosition()),
                points, mRoundInfo.target);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public PassesView mShots;
        public TextView mSubtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mShots = (PassesView) itemView.findViewById(R.id.shoots);
            mSubtitle = (TextView) itemView.findViewById(R.id.passe);
        }

        public void bindCursor(String passe, Shot[] points, int target) {
            mShots.setPoints(points, target);
            mSubtitle.setText(passe);
        }
    }
}
