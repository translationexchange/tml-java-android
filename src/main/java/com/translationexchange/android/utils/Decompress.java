package com.translationexchange.android.utils;

import android.content.Context;

import com.translationexchange.android.TmlAndroid;
import com.translationexchange.core.cache.CacheVersion;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

/**
 * Created by ababenko on 8/25/16.
 */
public class Decompress {
    private static final int BUFFER_SIZE = 1024 * 10;
    private static final String TAG = "Decompress";

    public static void unzipFromRes(Context context, String version, File destination) {
        String resName = String.format("tml_%s_tar_gz", version);
        int resId = context.getResources().getIdentifier(resName, "raw", context.getPackageName());
        if (resId != 0) {
            File versionFile = new File(destination.getPath(), "version.json");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            try {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(version));
                calendar.add(Calendar.HOUR, 1);
                calendar.add(Calendar.MINUTE, 30);
                org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
                jsonObject.put("t", calendar.getTimeInMillis());
                jsonObject.put("version", version);
                jsonObject.put("expired_in", calendar.getTimeInMillis() + CacheVersion.getVerificationInterval());
                FileUtils.writeToFile(versionFile, jsonObject.toString());
                InputStream inputStream = context.getResources().openRawResource(resId);
                unzip(inputStream, new File(destination, version).getPath());
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void unzip(String zipFile, String location) {
        try {
            FileInputStream fin = new FileInputStream(zipFile);
            unzip(fin, location);
        } catch (FileNotFoundException e) {
            TmlAndroid.getLogger().error(TAG, e.getMessage(), e);
        }
    }

    public static void unzip(InputStream stream, String destination) {
        dirChecker(destination, "");
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            GZIPInputStream inputStream = new GZIPInputStream(stream);
            TarArchiveInputStream is = new TarArchiveInputStream(inputStream);
            TarArchiveEntry tarArchiveEntry = null;

            while ((tarArchiveEntry = is.getNextTarEntry()) != null) {
                TmlAndroid.getLogger().debug(TAG, "Unzipping " + tarArchiveEntry.getName());

                if (tarArchiveEntry.isDirectory()) {
                    dirChecker(destination, tarArchiveEntry.getName());
                } else {
                    File f = new File(destination, tarArchiveEntry.getName());
                    if (!f.exists()) {
                        FileOutputStream fout = new FileOutputStream(f);
                        int count;
                        while ((count = is.read(buffer)) != -1) {
                            fout.write(buffer, 0, count);
                        }
                        fout.close();
                    }
                }
            }
            is.close();
        } catch (Exception e) {
            TmlAndroid.getLogger().error(TAG, "unzip = " + e.getMessage(), e);
        }
    }

    private static void dirChecker(String destination, String dir) {
        File f = new File(destination, dir);
        if (!f.isDirectory()) {
            boolean success = f.mkdirs();
            if (!success) {
                TmlAndroid.getLogger().warn(TAG, "Failed to create folder " + f.getName());
            }
        }
    }
}
