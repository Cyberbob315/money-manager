package com.example.iceman.project.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by iceman on 23/10/2016.
 */

public class PreferenceManager {
    public static final String FILE_NAME = "MONEY_MANAGEMENT";
    public static final String KEY_ISDELETE = "isDelete";
    private Context mContext;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public PreferenceManager(Context mContext) {
        this.mContext = mContext;
        preferences = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();

    }

    public void pushString(String key, String value) {

        editor.putString(key, value);
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public void pushBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key,false);
    }
}
