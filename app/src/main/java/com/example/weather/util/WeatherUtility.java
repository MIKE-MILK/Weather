package com.example.weather.util;

import android.content.Context;

import com.example.weather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;

public class WeatherUtility {

    public interface OnGetWeather{
        void onGetWeather(Weather weather);
    }

    private static final String TAG="WeatherUtlity";
    private static final String WEATHER_API_URL="https://api.heweather.net/s6/weather/now?location={0}&key=c9fe83f79d14475a9790ce5b18aa58f3";

    public static void GetWeather(Context context,String weatherId,OnGetWeather onGetWeather){
        BufferDataUtility.GetData(context,"weather", MessageFormat.format(WEATHER_API_URL,weatherId),true,(json,fromNetWork)->{
            String weatherJson=null;
            Weather weather=null;

            if (fromNetWork){
                weatherJson=ParseWeatherJson(json);
                weather=ParseWeather(weatherJson);
            }
            else{
                weather=ParseWeather(json);
            }
            if (weather!=null&&weather.status!=null&&weather.status.equals("ok")){
                onGetWeather.onGetWeather(weather);
            }else {
                RequestWeather(context,weatherId,onGetWeather);
            }
            if (fromNetWork){
                return weatherJson;
            }else {
                return null;
            }
        });
    }
    public static void RequestWeather(Context context,String weatherId,OnGetWeather onGetWeather){
        String url=MessageFormat.format(WEATHER_API_URL,weatherId);
        BufferDataUtility.RequestData(context,"weather",url,true,(json,fromNetWork)->{
            String weatherJson=ParseWeatherJson(json);
            Weather weather=ParseWeather(weatherJson);
            onGetWeather.onGetWeather(weather);
            if (weather!=null&&weather.status!=null&&weather.status.equals("ok")){
                onGetWeather.onGetWeather(weather);
                return weatherJson;
            }else {

                return null;
            }
        });
    }
    private static String ParseWeatherJson(String json){
        try {
            JSONObject jsonObject=new JSONObject(json);
            JSONArray jsonArray=jsonObject.getJSONArray("Weather");
            return jsonArray.getJSONObject(0).toString();
        }catch (Exception e){
            LogUtility.e(TAG,e.toString());
            return null;
        }
    }
    private static Weather ParseWeather(String weatherJosn){
        return new Gson().fromJson(weatherJosn,Weather.class);
    }
}
