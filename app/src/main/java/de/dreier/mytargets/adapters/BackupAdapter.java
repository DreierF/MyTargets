package de.dreier.mytargets.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ItemImageDetailsSecondaryActionBinding;
import de.dreier.mytargets.utils.backup.BackupEntry;

public class BackupAdapter extends RecyclerView.Adapter<BackupAdapter.BackupViewHolder> {

    private final OnItemClickListener<BackupEntry> primaryActionListener;
    private final OnItemClickListener<BackupEntry> secondaryActionListener;
    private LayoutInflater inflater;
    private DateFormat formatDateTime;
    private List<BackupEntry> backupEntries = new ArrayList<>();

    public BackupAdapter(Context context, OnItemClickListener<BackupEntry> primaryActionListener, OnItemClickListener<BackupEntry> secondaryActionListener) {
        this.inflater = LayoutInflater.from(context);
        this.primaryActionListener = primaryActionListener;
        this.secondaryActionListener = secondaryActionListener;
        this.formatDateTime = SimpleDateFormat.getDateTimeInstance();
    }

    @Override
    public BackupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_image_details_secondary_action, parent, false);
        return new BackupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BackupViewHolder holder, int position) {
        BackupEntry p = backupEntries.get(position);
        final String modified = formatDateTime.format(p.getModifiedDate());
        holder.binding.name.setText(modified);
        holder.binding.details.setText(p.getHumanReadableSize());
        holder.binding.primaryAction
                .setOnClickListener(view -> primaryActionListener.onItemClicked(p));
        holder.binding.secondaryAction
                .setOnClickListener(view -> secondaryActionListener.onItemClicked(p));
    }

    @Override
    public int getItemCount() {
        return backupEntries.size();
    }

    public void setList(List<BackupEntry> list) {
        this.backupEntries = list;
        notifyDataSetChanged();
    }

    public void remove(BackupEntry backupEntry) {
        int index = backupEntries.indexOf(backupEntry);
        if (index > -1) {
            backupEntries.remove(index);
            notifyItemRemoved(index);
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClicked(T item);
    }

    public static class BackupViewHolder extends RecyclerView.ViewHolder {

        public final ItemImageDetailsSecondaryActionBinding binding;

        public BackupViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}