package de.dreier.mytargets.adapters;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.drive.DriveId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.BackupActivity;
import de.dreier.mytargets.databinding.ItemRestoreBackupBinding;
import de.dreier.mytargets.utils.Backup;
import de.dreier.mytargets.utils.Utils;

import static de.dreier.mytargets.shared.SharedApplicationInstance.getContext;

public class BackupAdapter extends RecyclerView.Adapter<BackupAdapter.BackupViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private DateFormat formatDateTime;
    private List<Backup> backups;

    public BackupAdapter(Context context) {
        this.context = context;
        formatDateTime = SimpleDateFormat.getDateTimeInstance();
        inflater = LayoutInflater.from(getContext());
    }

    @Override
    public BackupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_restore_backup, parent, false);
        return new BackupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BackupViewHolder holder, int position) {
        Backup p = backups.get(position);
        final DriveId driveId = p.getDriveId();
        final String modified = formatDateTime.format(p.getModifiedDate());
        final String size = Utils.humanReadableByteCount(p.getBackupSize(), true);

        holder.binding.itemHistoryTime.setText(modified);
        holder.binding.itemHistoryType.setText(size);

        holder.binding.getRoot().setOnClickListener(v12 -> {
            // Show custom dialog
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_restore_backup);
            TextView createdTextView = (TextView) dialog
                    .findViewById(R.id.dialog_backup_restore_created);
            TextView sizeTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_size);
            Button restoreButton = (Button) dialog
                    .findViewById(R.id.dialog_backup_restore_button_restore);
            Button cancelButton = (Button) dialog
                    .findViewById(R.id.dialog_backup_restore_button_cancel);

            createdTextView.setText(modified);
            sizeTextView.setText(size);

            restoreButton.setOnClickListener(
                    v1 -> ((BackupActivity) context).downloadFromDrive(driveId.asDriveFile()));

            cancelButton.setOnClickListener(v121 -> dialog.dismiss());

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return backups.size();
    }

    public void setList(List<Backup> list) {
        this.backups = list;
        notifyDataSetChanged();
    }

    public static class BackupViewHolder extends RecyclerView.ViewHolder {

        public final ItemRestoreBackupBinding binding;

        public BackupViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}