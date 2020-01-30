package com.example.weather.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class BufferDataUtility {

    private static final String TAG = "BufferDataUtility";

    public interface OnGetDataListener{

        String onGetData(String data, boolean fromNetWork);
    }

    public static void GetData(Context context, String name, String requestUrl,boolean onUiThread, OnGetDataListener onGetDataListener) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String data = preferences.getString(name, null);
        if (data == null)
            RequestData(context, name, requestUrl, onUiThread, onGetDataListener);
        else {
            SaveData(editor, name, onGetDataListener.onGetData(data, false));
            LogUtility.d(TAG, "Get local data : " + name + "(" + data + ")");
        }
    }


    public static void RequestData(Context context, String name, String requestUrl,boolean onUiThread, OnGetDataListener onGetDataListener) {
        HttpUtility.SendRequest(requestUrl, (data) -> {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            if (onUiThread&&context instanceof Activity)
                ((Activity) context).runOnUiThread(() -> SaveData(editor, name, onGetDataListener.onGetData(data, true)));
            else
                SaveData(editor, name, onGetDataListener.onGetData(data, true));
            LogUtility.d(TAG, "Get Network data : " + name + "(" + data + ")");
        });
    }

    private static void SaveData(SharedPreferences.Editor editor,String name,String saveData) {
        if (editor != null && saveData != null) {
            editor.putString(name, saveData);
            editor.apply();
            LogUtility.d(TAG, "Save to local : " + name + "(" + saveData + ")");
        }
    }

}
