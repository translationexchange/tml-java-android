package com.translationexchange.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.translationexchange.android.service.TmlService;

import java.util.Locale;

public class PreferenceUtil {

    private static final String CURRENT_LOCATION = "CURRENT_LOCATION";

    private static final String USER_PREF_FILE_NAME = "com.translationexchange.android.PreferenceUtil";

    private static SharedPreferences sharedPreferences;

    private static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(USER_PREF_FILE_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void setCurrentLocation(Context context, Locale location) {
        String temp = location.getLanguage() + "," + location.getCountry();
        setUserPreference(context, CURRENT_LOCATION, temp);
        TmlService.startChangeLanguage(context);
    }

    public static Locale getCurrentLocation(Context context) {
        String temp = getUserPreference(context, CURRENT_LOCATION, String.class);
        if (temp.isEmpty()) {
            return Locale.getDefault();
        } else {
            String[] strings = temp.split(",");
            return new Locale(strings[0], strings[1]);
        }
    }

    public static <T> void setUserPreference(Context context, String key, T value) {
        init(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value.getClass().equals(String.class)) {
            editor.putString(key, (String) value);
        } else if (value.getClass().equals(Boolean.class)) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value.getClass().equals(Integer.class)) {
            editor.putInt(key, (Integer) value);
        } else {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
        editor.apply();
    }

    public static <T> T getUserPreference(Context context, String key, Class<?> clazz) {
        init(context);
        if (clazz.equals(String.class)) {
            String value = sharedPreferences.getString(key, "");
            return (T) value;
        } else if (clazz.equals(Boolean.class)) {
            Boolean value = sharedPreferences.getBoolean(key, false);
            return (T) value;
        } else if (clazz.equals(Integer.class)) {
            Integer value = sharedPreferences.getInt(key, -1);
            return (T) value;
        } else {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
    }

    public static <T> T getUserPreference(Context context, String key, Class<?> clazz, T defaultValue) {
        init(context);
        Object value = null;
        if (clazz.equals(String.class)) {
            value = sharedPreferences.getString(key, (String) defaultValue);
        } else if (clazz.equals(Boolean.class)) {
            value = sharedPreferences.getBoolean(key, (Boolean) defaultValue);
        } else if (clazz.equals(Integer.class)) {
            value = sharedPreferences.getInt(key, (Integer) defaultValue);
        } else {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
        return (T) value;
    }

    public static void clearUserPreference(Context context, String key) {
        init(context);
        sharedPreferences.edit().remove(key).apply();
    }

    public static void clearUserPreferences(Context context) {
        init(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
