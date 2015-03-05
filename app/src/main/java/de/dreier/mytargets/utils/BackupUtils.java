package de.dreier.mytargets.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;

/**
 * Created by Florian on 05.03.2015.
 */
public class BackupUtils {

    private static final String FOLDER_NAME = "MyTargets";

    public static boolean Import(Activity a, Uri uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        try {
            InputStream st = a.getContentResolver().openInputStream(uri);
            if (!DatabaseManager.Import(st))
                throw new IllegalStateException();

            Toast.makeText(a, R.string.import_successful, Toast.LENGTH_SHORT).show();
            return true;
        } catch (FileNotFoundException ioe) {
            builder.setTitle(R.string.import_failed);
            builder.setMessage(a.getString(R.string.file_not_found));
        } catch (Exception e) {
            builder.setTitle(R.string.import_failed);
            builder.setMessage(a.getString(R.string.failed_reading_file));
        }
        builder.setNegativeButton("", null);
        builder.setPositiveButton(android.R.string.ok, null).show();
        return false;
    }

    public static void Backup(Activity a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            GregorianCalendar c = new GregorianCalendar();

            String dir = Environment.getExternalStorageDirectory().toString() + "/" + FOLDER_NAME + "/";
            String file = dir + "backup_" + c.get(Calendar.YEAR) + "_"
                    + (c.get(Calendar.MONTH) + 1) + "_" + c.get(Calendar.DATE)
                    + ".db";
            try {
                File f = new File(dir);
                //noinspection ResultOfMethodCallIgnored
                f.mkdir();
                if (!f.exists() || !f.isDirectory()) {
                    throw new IOException(a.getString(R.string.dir_not_created));
                }

                File db = a.getDatabasePath(DatabaseManager.DATABASE_NAME);
                copy(db, new File(file));

                builder.setTitle(R.string.backup_successful);
                builder.setMessage(a.getString(R.string.backup_saved_as, file));
            } catch (IOException e) {
                builder.setTitle(R.string.backup_failed);
                builder.setMessage(a.getString(R.string.backup_error, e.getMessage()));
            }
        } else {
            builder.setTitle(R.string.sd_card_not_available_title);
            builder.setMessage(R.string.sd_card_not_available_desc);
        }
        builder.setNegativeButton(null, null);
        builder.setPositiveButton(android.R.string.ok, null).show();
    }


    public static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public static void copy(InputStream in, File dst) throws IOException {
        OutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static Uri export(Context context) throws IOException {
        DatabaseManager db = DatabaseManager.getInstance(context);
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
        String fileName = "/" + FOLDER_NAME + "/exported_data_" + format.format(new Date()) + ".csv";
        File file = new File(baseDir + fileName);
        db.exportAll(file);
        return Uri.fromFile(file);
    }
}
