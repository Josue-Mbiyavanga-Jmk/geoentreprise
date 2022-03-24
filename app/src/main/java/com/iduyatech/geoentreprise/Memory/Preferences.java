package com.iduyatech.geoentreprise.Memory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static Context ctx = null;

    public static Context getContext() {
        return ctx;
    }

    @SuppressLint("CommitPrefEdits")
    public static void initialize(Context con) {
        if (null == preferences) {
            preferences = PreferenceManager.getDefaultSharedPreferences(con);
        }
        if (null == editor) {
            editor = preferences.edit();
        }
        if (null == ctx) {
            ctx = con;
        }
    }

    public static void save(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public static void save(String key, Integer value) {
        save(key, String.valueOf(value));
    }

    public static void save(String key, Long value) {
        save(key, String.valueOf(value));
    }

    public static String get(String key) {
        return preferences.getString(key, null);
    }

    public static Boolean contains(String key) {
        return preferences.contains(key);
    }

    public static void removeKey(String key) {
        if (contains(key)) {
            editor.remove(key);
            editor.commit();
        }
    }
    public static void removeKeys(String... keys) {
        for (String key : keys) {
            if (contains(key))
                removeKey(key);
        }
    }
}
