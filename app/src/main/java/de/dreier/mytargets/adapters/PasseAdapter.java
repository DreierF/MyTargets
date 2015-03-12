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
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.views.PassesView;

/**
 * Shows all passes of one round
 */
public class PasseAdapter extends CursorAdapter {

    final LayoutInflater mInflater;
    final DatabaseManager db;
    final Context mContext;
    private final int passeIdInd;
    private final Round mRoundInfo;
    String mNewText;

    public PasseAdapter(Context context, long round, Round roundInfo) {
        super(context, DatabaseManager.getInstance(context).getPasses(round), 0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = DatabaseManager.getInstance(context);
        mContext = context;
        mNewText = context.getString(R.string.new_passe);
        mRoundInfo = roundInfo;
        passeIdInd = getCursor().getColumnIndex(DatabaseManager.PASSE_ID);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.passe_card, viewGroup, false);
        holder.shots = (PassesView) v.findViewById(R.id.shoots);
        holder.subtitle = (TextView) v.findViewById(R.id.passe);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.subtitle.setText(context.getString(R.string.passe) + " " + (1 + cursor.getPosition()));
        Shot[] points = db.getPasse(cursor.getLong(passeIdInd));
        holder.shots.setPoints(points, mRoundInfo.target);
    }

    public static class ViewHolder {
        public PassesView shots;
        public TextView subtitle;
    }
}
