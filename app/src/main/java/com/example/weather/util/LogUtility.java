package com.example.weather.util;

import android.util.Log;

public class LogUtility {
    public static final int LEVEL_ERROR=5;
    public static final int LEVEL_WARN=4;
    public static final int LEVEL_INFO=3;
    public static final int LEVEL_DEBUG=2;
    public static final int LEVEL_VERBOSE=1;
    public static final int LEVEL_NONE=0;

    public static final int LEVEL=LEVEL_ERROR;

    public static void e(String tag, String s) {
        if (LEVEL >= LEVEL_ERROR)
            Log.e(tag, s);
    }
    public static void w(String tag, String s) {
        if (LEVEL >= LEVEL_WARN)
            Log.w(tag, s);
    }
    public static void i(String tag, String s) {
        if (LEVEL >= LEVEL_INFO)
            Log.i(tag, s);
    }
    public static void d(String tag, String s) {
        if (LEVEL >= LEVEL_DEBUG)
            Log.d(tag, s);
    }
    public static void v(String tag, String s) {
        if (LEVEL >= LEVEL_VERBOSE)
            Log.v(tag, s);
    }
}
