/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;


public class BackupUtils {

    private static final String FOLDER_NAME = "MyTargets";
    private static final int BUFFER = 1024;

    public static boolean Import(Activity a, Uri uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        try {
            InputStream st = a.getContentResolver().openInputStream(uri);
            if (!DatabaseManager.Import(a, st)) {
                throw new IllegalStateException();
            }

            Toast.makeText(a, R.string.import_successful, Toast.LENGTH_SHORT).show();
            return true;
        } catch (FileNotFoundException ioe) {
            ioe.printStackTrace();
            builder.setTitle(R.string.import_failed);
            builder.setMessage(a.getString(R.string.file_not_found));
        } catch (Exception e) {
            e.printStackTrace();
            builder.setTitle(R.string.import_failed);
            builder.setMessage(a.getString(R.string.failed_reading_file));
        }
        builder.setNegativeButton("", null);
        builder.setPositiveButton(android.R.string.ok, null).show();
        return false;
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
        String fileName =
                "/" + FOLDER_NAME + "/exported_data_" + format.format(new Date()) + ".csv";
        File f = new File(baseDir);
        f.mkdir();
        if (!f.exists() || !f.isDirectory()) {
            throw new IOException(context.getString(R.string.dir_not_created));
        }
        File file = new File(baseDir + fileName);
        db.exportAll(file);
        return Uri.fromFile(file);
    }

    public static Uri backup(Context context) throws IOException {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
        String fileName = baseDir + "/" + FOLDER_NAME + "/backup_" + format.format(new Date()) + ".zip";
        File f = new File(baseDir + "/" + FOLDER_NAME);
        f.mkdir();
        if (!f.exists() || !f.isDirectory()) {
            throw new IOException(context.getString(R.string.dir_not_created));
        }

        zip(context, fileName);

        return Uri.fromFile(new File(fileName));
    }

    private static void zip(Context context, String zipFileName) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            File db = context.getDatabasePath(DatabaseManager.DATABASE_NAME);
            FileInputStream fi = new FileInputStream(db);
            origin = new BufferedInputStream(fi, BUFFER);

            ZipEntry entry = new ZipEntry("/data.db");
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();

            String[] files = DatabaseManager.getInstance(context).getImages();
            for (String file : files) {
                fi = new FileInputStream(new File(context.getFilesDir(), file));
                origin = new BufferedInputStream(fi, BUFFER);

                entry = new ZipEntry("/" + file);
                out.putNextEntry(entry);

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File unzip(Context context, InputStream in) {
        File tmpDb = null;
        try {
            tmpDb = File.createTempFile("import", ".db");

            ZipInputStream zin = new ZipInputStream(in);
            ZipEntry sourceEntry;
            while (true) {

                sourceEntry = zin.getNextEntry();

                if (sourceEntry == null) {
                    break;
                }

                if (sourceEntry.isDirectory()) {
                    zin.closeEntry();
                    continue;
                }

                FileOutputStream fOut;
                if (sourceEntry.getName().endsWith(".db")) {
                    // Write database to tmp file
                    fOut = new FileOutputStream(tmpDb);
                } else {
                    // Write all other files(images) to files dir in apps data
                    int start = sourceEntry.getName().lastIndexOf("/") + 1;
                    String name = sourceEntry.getName().substring(start);
                    Log.d("", name);
                    fOut = context.openFileOutput(name, Context.MODE_PRIVATE);
                }

                final OutputStream targetStream = fOut;
                try {
                    int read;
                    while (true) {
                        byte[] buffer = new byte[1024];
                        read = zin.read(buffer);
                        if (read == -1) {
                            break;
                        }
                        targetStream.write(buffer, 0, read);
                    }
                    targetStream.flush();
                } finally {
                    targetStream.close();
                }
                zin.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return tmpDb;
    }
}
