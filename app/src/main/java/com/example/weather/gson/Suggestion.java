package com.example.weather.gson;

import android.webkit.WebView;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    public class Comfort{
        public String txt;
    }
    public class Carwash{
        public String txt;
    }
    public class Sport{
        public String txt;
    }
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public Carwash carwash;
    public Sport sport;
}
