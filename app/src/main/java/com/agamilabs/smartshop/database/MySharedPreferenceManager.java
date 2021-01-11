package com.agamilabs.smartshop.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.agamilabs.smartshop.constants.AppConstants;

public class MySharedPreferenceManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public MySharedPreferenceManager(Context context) {
        this.context = context;
    }

    public String getUserNo() {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(AppConstants.USER_NO, "-1");
    }

    public String getStoreNo() {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(AppConstants.Store.STORE_NO, "-1");
    }

    public String getUserLat() {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        //return sharedPreferences.getString(AppConstants.LATITUDE, "22.473481");
        return sharedPreferences.getString(AppConstants.LATITUDE, "0");
    }

    public String getUserLon() {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        //return sharedPreferences.getString(AppConstants.LONGITUDE, "91.807723");
        return sharedPreferences.getString(AppConstants.LONGITUDE, "0");
    }

    public String getFCMToken(String userNo) {

        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(AppConstants.FCM_TOKEN + userNo, "");
    }

    public String getFCMToken() {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstants.FCM_TOKEN, "");
    }

    public void saveUserFCMToken(String userno, String token) {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString(AppConstants.FCM_TOKEN + userno, token);
        editor.putString(AppConstants.FCM_TOKEN, token);

        editor.apply();
    }

    public void saveUserLatitude(String lat) {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString(AppConstants.LATITUDE, lat);

        editor.apply();
    }

    public void saveUserLongitude(String lon) {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString(AppConstants.LONGITUDE, lon);

        editor.apply();
    }

    public void saveLoggedIn(boolean flag) {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putBoolean(AppConstants.USER_LOGGED_IN, flag);

        editor.apply();
    }

    public boolean isLoggedIn(){
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(AppConstants.USER_LOGGED_IN, false);
    }
}