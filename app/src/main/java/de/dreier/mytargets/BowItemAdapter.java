package de.dreier.mytargets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class BowItemAdapter extends CursorAdapter implements SpinnerAdapter {

    private final LayoutInflater mInflater;

    public BowItemAdapter(Context context) {
        super(context,new TargetOpenHelper(context).getBows(),0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position,convertView,parent);
	}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = mInflater.inflate(R.layout.bow_item, null);

        ViewHolder holder = new ViewHolder();
        holder.img = (ImageView) v.findViewById(R.id.bowImage);
        holder.desc = (TextView) v.findViewById(R.id.bowName);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
       // holder.desc.setText("Bogen");
    }

    private class ViewHolder {
        ImageView img;
        TextView desc;
    }
}