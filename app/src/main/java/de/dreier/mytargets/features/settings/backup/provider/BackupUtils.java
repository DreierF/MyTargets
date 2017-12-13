/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.settings.backup.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import de.dreier.mytargets.shared.streamwrapper.Stream;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.db.ArrowImage;
import de.dreier.mytargets.shared.models.db.BowImage;
import de.dreier.mytargets.shared.models.db.EndImage;
import de.dreier.mytargets.shared.utils.FileUtils;

public class BackupUtils {

    private static final int BUFFER = 1024;

    public static void copy(@NonNull File src, @NonNull File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.flush();
        outStream.close();
    }

    public static void copy(@NonNull InputStream in, @NonNull OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.flush();
        in.close();
        out.close();
    }

    @NonNull
    static String getBackupName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        return "MyTargets_backup_" + format.format(new Date()) + ".zip";
    }

    public static void importZip(@NonNull Context context, InputStream in) throws IOException {
        // Unzip all images and database
        File file = unzip(context, in);

        // Replace database file
        File db_file = context.getDatabasePath(AppDatabase.DATABASE_IMPORT_FILE_NAME);
        FileUtils.copy(file, db_file);
    }

    /**
     * Returns a list of file names, which are implicitly placed in the ../files/ folder of the app.
     */
    public static String[] getImages() {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Stream.of(SQLite.select()
                .from(BowImage.class)
                .queryList())
                .flatMap(bow -> Stream.of(bow.getFileName()))
                .toList());
        list.addAll(Stream.of(SQLite.select()
                .from(EndImage.class)
                .queryList())
                .flatMap(end -> Stream.of(end.getFileName()))
                .toList());
        list.addAll(Stream.of(SQLite.select()
                .from(ArrowImage.class)
                .queryList())
                .flatMap(arrow -> Stream.of(arrow.getFileName()))
                .toList());
        return list.toArray(new String[list.size()]);
    }

    public static void zip(@NonNull Context context, @NonNull OutputStream dest) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
        try {
            BufferedInputStream origin;
            byte data[] = new byte[BUFFER];

            File db = context.getDatabasePath(AppDatabase.DATABASE_FILE_NAME);
            FileInputStream fi = new FileInputStream(db);
            origin = new BufferedInputStream(fi, BUFFER);

            ZipEntry entry = new ZipEntry("/data.db");
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();

            String[] files = getImages();
            for (String file : files) {
                try {
                    fi = new FileInputStream(new File(context.getFilesDir(), file));
                    origin = new BufferedInputStream(fi, BUFFER);

                    entry = new ZipEntry("/" + file);
                    out.putNextEntry(entry);

                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            out.close();
        } finally {
            safeCloseClosable(out);
        }
    }

    private static void safeCloseClosable(@Nullable Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private static File unzip(@NonNull Context context, InputStream in) throws IOException {
        File tmpDb = null;
        int dbFiles = 0;
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
                    dbFiles++;
                } else {
                    // Write all other files(images) to files dir in apps data
                    int start = sourceEntry.getName().lastIndexOf("/") + 1;
                    String name = sourceEntry.getName().substring(start);
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
                    safeCloseClosable(targetStream);
                }
                zin.closeEntry();
            }
        } finally {
            safeCloseClosable(in);
        }
        if (dbFiles != 1) {
            throw new IllegalStateException("Input file is not a valid backup");
        }
        return tmpDb;
    }
}
