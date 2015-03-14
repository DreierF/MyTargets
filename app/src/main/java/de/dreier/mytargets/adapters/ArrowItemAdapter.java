package de.dreier.mytargets.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Arrow;

public class ArrowItemAdapter extends ArrayAdapter<Arrow> {

    public ArrowItemAdapter(Context context) {
        super(context, R.layout.image_item, DatabaseManager.getInstance(context).getArrows());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ImageView img = (ImageView) view.findViewById(R.id.image);
        TextView name = (TextView) view.findViewById(R.id.name);

        Arrow item = getItem(position);
        name.setText(item.name);
        img.setImageBitmap(item.image);
        return view;
    }
}