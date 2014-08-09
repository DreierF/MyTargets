package de.dreier.mytargets;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Shows all passes of one round
 */
public class PasseAdapter extends NowListAdapter {

    private final int passeIdInd;
    private final TargetOpenHelper.Round roundInfo;
    private final long mRound;
    private final long mTraining;
    private final float density;

    public PasseAdapter(Context context, long training, long round) {
        super(context,new TargetOpenHelper(context).getPasses(round));
        roundInfo = db.getRound(round);
        mExtraCards = 2;
        mNewText = context.getString(R.string.new_passe);
        mRound = round;
        mTraining = training;
        passeIdInd = getCursor().getColumnIndex(TargetOpenHelper.PASSE_ID);
        density = context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected View buildExtraCard(int pos, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.round_info_card, parent, false);
        } else {
            view = convertView;
        }

        int[] target = TargetView.target_points[roundInfo.target];
        int reached = db.getRoundPoints(mRound,roundInfo.target);
        int maxP = roundInfo.ppp*target[0]*db.getPasses(mRound).getCount();

        TextView round = (TextView) view.findViewById(R.id.round);
        TextView dist = (TextView) view.findViewById(R.id.dist);
        TextView points = (TextView) view.findViewById(R.id.gesRound);
        TextView tar = (TextView) view.findViewById(R.id.target);
        round.setText(mContext.getString(R.string.round)+" "+db.getRoundInd(mTraining,mRound));
        dist.setText(Html.fromHtml(mContext.getString(R.string.distance)+": <font color=#669900><b>"+
                roundInfo.distance+" - "+
                mContext.getString(roundInfo.indoor?R.string.indoor:R.string.outdoor)+"</b></font>"));
        points.setText(Html.fromHtml(mContext.getString(R.string.points)+": <font color=#669900><b>"+reached+"/"+maxP+"</b></font>"));
        tar.setText(Html.fromHtml(mContext.getString(R.string.target_round)+": <font color=#669900><b>"+ TargetItemAdapter.targets[roundInfo.target]+"</b></font>"));
        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.passe_card, viewGroup, false);
        holder.layout = (LinearLayout)v.findViewById(R.id.passe_layout);
        holder.shots = (PassenView) v.findViewById(R.id.shoots);
        holder.subtitle = (TextView) v.findViewById(R.id.passe);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.layout.getLayoutParams();
        params.setMargins((int) (8 * density), (int) (cursor.getPosition() == 0 ? 8 * density : 0), (int) (8 * density), (int) (cursor.getPosition() == cursor.getCount() - 1 ? 8 * density : 0));
        holder.subtitle.setText(context.getString(R.string.passe)+" "+(1+cursor.getPosition()));
        int[] points = db.getPasse(cursor.getLong(passeIdInd));
        holder.shots.setPoints(points,roundInfo.target);
    }

    public static class ViewHolder {
        public PassenView shots;
        public TextView subtitle;
        public LinearLayout layout;
    }
}
