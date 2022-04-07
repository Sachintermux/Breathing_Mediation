package com.cipherapps.breathingmeditation.savedata;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefDataSave {

    public void savedData( Context context,String TAG, String data ){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG, data);
        editor.apply();
    }

    public void savedData( Context context,String TAG, int data ){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(TAG, data);
        editor.apply();
    }

    public int getData(Context context, String TAG, int defaultData){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int data = preferences.getInt(TAG, defaultData);
        return data;
    }

    public String getData(Context context, String TAG, String defaultData){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String position = preferences.getString(TAG,defaultData);
        return String.valueOf(position);

    }
}
