package com.example.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.weather.gson.Forecast;
import com.example.weather.gson.Weather;
import com.example.weather.service.UpdateService;
import com.example.weather.util.BufferDataUtility;
import com.example.weather.util.WeatherUtility;

public class WeatherActivity extends AppCompatActivity
{
    private Button button;
    private TextView City;
    private  TextView Time;
    private TextView Temp;
    private TextView Info;
    private LinearLayout forecastList;
    private TextView aqiAqi;
    private TextView aqipm25;
    private TextView Comfort;
    private TextView Carwash;
    private TextView Sport;
    public DrawerLayout weatherDrawer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageView;
    private String weatherId;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver updateRecever=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RefreshWeather(weatherId);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        button=findViewById(R.id.title_nav_btn);
        City=findViewById(R.id.title_city);
        Time=findViewById(R.id.title_time);
        Temp=findViewById(R.id.now_temp);
        Info=findViewById(R.id.now_info);
        forecastList=findViewById(R.id.forecast_list_layout);
        aqiAqi=findViewById(R.id.aqi_aqi);
        aqipm25=findViewById(R.id.aqi_pm25);
        Comfort=findViewById(R.id.suggestion_comfort);
        Carwash=findViewById(R.id.suggestion_carwash);
        Sport=findViewById(R.id.suggestion_sport);
        weatherDrawer=findViewById(R.id.weather_drawerlayout);
        imageView=findViewById(R.id.weather_image);
        swipeRefreshLayout=findViewById(R.id.weather_swiperefresh);
        weatherId=getIntent().getStringExtra("weatherId");

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(()->RefreshWeather(weatherId));
        button.setOnClickListener(v->weatherDrawer.openDrawer(GravityCompat.START));
        if (Build.VERSION.SDK_INT>=21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        IntentFilter filter=new IntentFilter("UPDATE_WEATHER");
        localBroadcastManager.registerReceiver(updateRecever,filter);
        Intent intent=new Intent(this, UpdateService.class);
        startService(intent);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("weatherId",getIntent().getStringExtra("weatherId")).apply();
        LoadWeather(weatherId);
    }
    protected void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(updateRecever);
    }
    public void RefreshWeather(String weatherId){
        swipeRefreshLayout.setRefreshing(true);
        WeatherUtility.RequestWeather(this,weatherId,weather -> ShowWeather(weather));
        BufferDataUtility.RequestData(this,"weatherImage","http://guolin.tech/api/bing_pic",true,(data,t)->{
            ShowImage(data);
            return data;
        });
    }
    private void LoadWeather(String weatherId){
        swipeRefreshLayout.setRefreshing(true);
        WeatherUtility.GetWeather(this,weatherId,weather -> ShowWeather(weather));
        BufferDataUtility.GetData(this,"weatherImage","http://guolin.tech/api/bing_pic",true,(data,fromNetwork) -> {
            ShowImage(data);
            if(fromNetwork)
                return data;
            else
                return null;
        });
    }
    private void ShowWeather(Weather weather){
        if (weather!=null){
            City.setText(weather.basic.cityName);
            Temp.setText(weather.now.temperature);
            Time.setText(weather.basic.update.updateName.split(" ")[1]);
            Info.setText(weather.now.more.info);
            forecastList.removeAllViews();
            for (Forecast forecast:weather.forecastList){
                AddForecastItem(forecast);
                aqiAqi.setText(weather.aqi.city.aqi);
                aqipm25.setText(weather.aqi.city.pm25);
                Comfort.setText("舒适度"+weather.suggestion.comfort.txt);
                Carwash.setText("洗车建议"+weather.suggestion.carwash.txt);
                Sport.setText("运动建议"+weather.suggestion.sport.txt);
            }
        }else {
            Toast.makeText(this,"获取天气失败",Toast.LENGTH_SHORT).show();
        }
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    private void ShowImage(String imageData){
        Glide.with(this).load(imageData).into(imageView);
    }
    private void AddForecastItem(Forecast forecast){
        View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastList,false);
        TextView date=view.findViewById(R.id.forecast_item_date);
        TextView info=view.findViewById(R.id.forecast_item_info);
        TextView temp=view.findViewById(R.id.forecast_item_temp);
        date.setText(forecast.date);
        info.setText(forecast.more.info);
        temp.setText(forecast.temperature.min+"℃");
        temp.setText(forecast.temperature.max+"℃");
        forecastList.addView(view);
    }
}

