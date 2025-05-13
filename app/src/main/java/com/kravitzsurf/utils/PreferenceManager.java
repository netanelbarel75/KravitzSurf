package com.kravitzsurf.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    
    private static final String PREF_NAME = "KravitzSurfPrefs";
    private static final String KEY_NEXT_CLASS_ID = "next_class_id";
    private static final String KEY_NEXT_CLASS_TIME = "next_class_time";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    public void setNextClassId(String classId) {
        editor.putString(KEY_NEXT_CLASS_ID, classId);
        editor.apply();
    }
    
    public String getNextClassId() {
        return sharedPreferences.getString(KEY_NEXT_CLASS_ID, null);
    }
    
    public void setNextClassTime(long time) {
        editor.putLong(KEY_NEXT_CLASS_TIME, time);
        editor.apply();
    }
    
    public long getNextClassTime() {
        return sharedPreferences.getLong(KEY_NEXT_CLASS_TIME, 0);
    }
    
    public void setUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }
    
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }
    
    public void setUserEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }
    
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }
    
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}
