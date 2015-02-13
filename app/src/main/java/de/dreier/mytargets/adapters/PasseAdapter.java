package de.dreier.mytargets.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Bow;
import de.dreier.mytargets.views.PassesView;
import de.dreier.mytargets.R;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.utils.TargetOpenHelper;

/**
 * Shows all passes of one round
 */
public class PasseAdapter extends NowListAdapter {

    private final int passeIdInd;
    private final Round mRoundInfo;
    private final long mRound;
    private final long mTraining;

    public PasseAdapter(Context context, long training, long round, Round roundInfo) {
        super(context, new TargetOpenHelper(context).getPasses(round));
        mExtraCards = 2;
        mNewText = context.getString(R.string.new_passe);
        mRound = round;
        mRoundInfo = roundInfo;
        mTraining = training;
        passeIdInd = getCursor().getColumnIndex(TargetOpenHelper.PASSE_ID);
    }

    @Override
    protected View buildExtraCard(int pos, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.round_info_card, parent, false);
        } else {
            view = convertView;
        }

        int maxPoints = Target.getMaxPoints(mRoundInfo.target);
        int reached = db.getRoundPoints(mRound);
        int maxP = mRoundInfo.ppp * maxPoints * db.getPasses(mRound).getCount();

        TextView round = (TextView) view.findViewById(R.id.detail_round);
        TextView info = (TextView) view.findViewById(R.id.detail_round_info);
        TextView score = (TextView) view.findViewById(R.id.detail_score);

        // Set round info
        round.setText(mContext.getString(R.string.round) + " " + db.getRoundInd(mTraining, mRound));
        String infoText = mContext.getString(R.string.distance) + ": <font color=#669900><b>" +
                mRoundInfo.distance + " - " +
                mContext.getString(mRoundInfo.indoor ? R.string.indoor : R.string.outdoor) + "</b></font><br>" +
                mContext.getString(R.string.points) + ": <font color=#669900><b>" + reached + "/" + maxP + "</b></font><br>" +
                mContext.getString(R.string.target_round) + ": <font color=#669900><b>" + TargetItemAdapter.targets[mRoundInfo.target] + "</b></font>";
        Bow binfo = db.getBow(mRoundInfo.bow, true);
        if (binfo != null) {
            infoText += "<br>" + mContext.getString(R.string.bow) +
                    ": <font color=#669900><b>" + TextUtils.htmlEncode(binfo.name) + "</b></font>";
        }
        info.setText(Html.fromHtml(infoText));

        // Set number of X, 10, 9 shoots
        infoText = "X: <font color=#669900><b>" + mRoundInfo.scoreCount[0] + "</b></font><br>" +
                mContext.getString(R.string.ten_x) + ": <font color=#669900><b>" + (mRoundInfo.scoreCount[0]+mRoundInfo.scoreCount[1]) + "</b></font><br>" +
                mContext.getString(R.string.nine) + ": <font color=#669900><b>" + mRoundInfo.scoreCount[2] + "</b></font>";
        score.setText(Html.fromHtml(infoText));
        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.passe_card, viewGroup, false);
        holder.layout = (LinearLayout) v.findViewById(R.id.passe_layout);
        holder.shots = (PassesView) v.findViewById(R.id.shoots);
        holder.subtitle = (TextView) v.findViewById(R.id.passe);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.subtitle.setText(context.getString(R.string.passe) + " " + (1 + cursor.getPosition()));
        int[] points = db.getPasse(cursor.getLong(passeIdInd));
        holder.shots.setPoints(points, mRoundInfo.target, mRoundInfo.compound);
    }

    public static class ViewHolder {
        public PassesView shots;
        public TextView subtitle;
        public LinearLayout layout;
    }
}
