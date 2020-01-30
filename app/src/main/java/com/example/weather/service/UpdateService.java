package com.example.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.weather.gson.Weather;
import com.example.weather.util.BufferDataUtility;
import com.example.weather.util.LogUtility;
import com.example.weather.util.WeatherUtility;

public class UpdateService extends Service {
    private static final String TAG = "UpdateService";
    private LocalBroadcastManager localBroadcastManager;
    private Intent intent;

    @Override
    public void onCreate() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intent = new Intent("UPDATE_WEATHER");
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherId = preferences.getString("weather", null);
        if (weatherId != null) {
            WeatherUtility.RequestWeather(this, weatherId, (weather) -> {});
        }
            BufferDataUtility.RequestData(this, "weatherImage", "http://guolin.tech/api/bing_pic", false, (data, t) -> data);
            localBroadcastManager.sendBroadcast(intent);
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            int wakeTime = 4 * 60 * 60 * 1000;
            long triggerTime = wakeTime + SystemClock.elapsedRealtime();
            Intent i = new Intent(this, UpdateService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            manager.cancel(pi);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
            LogUtility.d(TAG, "Active Service");
            return super.onStartCommand(intent, flags, startId);

    }
}
