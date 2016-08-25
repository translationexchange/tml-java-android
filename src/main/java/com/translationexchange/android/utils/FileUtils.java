package com.translationexchange.android.utils;

import android.content.Context;
import android.os.Environment;

import com.translationexchange.android.TmlAndroid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by ababenko on 3/23/2016.
 */
public class FileUtils {
    // Storage states
    private static boolean externalStorageAvailable;
    private static boolean externalStorageWriteable;

    /**
     * Checks the external storage's state and saves it in member attributes.
     */
    private static void checkStorage() {
        // Get the external storage's state
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // Storage is available and writeable
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            // Storage is only readable
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            // Storage is neither readable nor writeable
            externalStorageAvailable = externalStorageWriteable = false;
        }
    }

    /**
     * Checks the state of the external storage.
     *
     * @return True if the external storage is available, false otherwise.
     */
    public static boolean isExternalStorageAvailable() {
        checkStorage();
        return externalStorageAvailable;
    }

    /**
     * Checks the state of the external storage.
     *
     * @return True if the external storage is writeable, false otherwise.
     */
    public static boolean isExternalStorageWriteable() {
        checkStorage();
        return externalStorageWriteable;
    }

    /**
     * Checks the state of the external storage.
     *
     * @return True if the external storage is available and writeable, false
     * otherwise.
     */
    public static boolean isExternalStorageAvailableAndWriteable() {
        checkStorage();
        return externalStorageAvailable && externalStorageWriteable;
    }

    public static File getBaseDirectory(Context context) {
        File file = new File(isExternalStorageAvailableAndWriteable() ? (Environment.getExternalStorageDirectory() + File.separator + "TML") : (context.getFilesDir() + File.separator + "TML"));
        if (!file.exists()) {
            TmlAndroid.getLogger().error("FileUtils", "Creating base directory - " + file.mkdir());
        }
        return file;
    }

    public static File getLogFile(Context context) {
        return new File(getBaseDirectory(context), "TML_sample_application_log.txt");
    }

    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            if (inputChannel != null)
                inputChannel.close();
            if (outputChannel != null)
                outputChannel.close();
        }
    }

    public static void writeToFile(File file, String text) throws IOException {
        BufferedWriter buf = new BufferedWriter(new FileWriter(file));
        buf.append(text);
        buf.close();
    }
}
