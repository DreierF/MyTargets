
package com.michaelflisar.licenses.dialog;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.michaelflisar.licenses.ViewHolder;
import com.michaelflisar.licenses.licenses.LicenseEntry;
import com.michaelflisar.licensesdialog.R;

public class AdapterLicenses extends BaseExpandableListAdapter
{
    private Context mContext;
    private List<LicenseEntry> mLicenses;

    public AdapterLicenses(Context context, List<LicenseEntry> licenses) {
        mContext = context;
        mLicenses = licenses;
    }

    public void updateList(List<LicenseEntry> licenses) {
        mLicenses = licenses;
        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mLicenses.get(groupPosition);
    }
    
    public void setChild(int groupPosition, LicenseEntry entry) {
        mLicenses.set(groupPosition, entry);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_license_data, null);

        LicenseEntry entry = mLicenses.get(groupPosition);
        
        // getting view references
        TextView tvLicense = ViewHolder.get(convertView, R.id.tvLicense);

        // updating views
        tvLicense.setText(entry.getLicense().getText());
        
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return mLicenses.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return mLicenses.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_license, null);

        LicenseEntry entry = mLicenses.get(groupPosition);
        
        // getting view references
        TextView tvLibrary = ViewHolder.get(convertView, R.id.tvLibrary);
        TextView tvAuthor = ViewHolder.get(convertView, R.id.tvAuthor);
        TextView tvLink = ViewHolder.get(convertView, R.id.tvLink);
        TextView tvVersion = ViewHolder.get(convertView, R.id.tvVersion);
        TextView tvLicenseType = ViewHolder.get(convertView, R.id.tvLicenseType);
        
        // updating views
        tvLibrary.setText(entry.getLibraryName());
        tvAuthor.setText(entry.getLibraryAuthor());
        tvVersion.setText(entry.getLibraryVersion());
        tvLink.setText(entry.getLibraryLink());
        // does not work for custom links if only set in xml...
        tvLink.setFocusable(false);
        tvLink.setFocusableInTouchMode(false);
        
        tvLicenseType.setText(entry.getLicense().getName());
        
        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

}
