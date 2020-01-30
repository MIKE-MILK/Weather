package com.example.weather.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.WeatherActivity;
import com.example.weather.db.City;
import com.example.weather.db.Country;
import com.example.weather.db.Province;
import com.example.weather.util.CityUility;

public class ChooseAreaFragment extends Fragment {
    private static final int LEVEL_PROVINCE=0;
    private static final int LEVEL_CITY=1;
    private static final int LEVEL_COUNTRY=2;
    private TextView textView;
    private Button button;
    private RecyclerView cityList;
    private ProgressDialog progressDialog;
    private int level;
    private Province chooseProvince;
    private City chooseCity;
    private Country chooseCountry;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Build saveInstanceState){
        View view=inflater.inflate(R.layout.choose_area,container,false);
        textView=view.findViewById(R.id.title_text);
        button=view.findViewById(R.id.back_button);
        cityList=view.findViewById(R.id.choose_area_list);
        cityList.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        cityList.setLayoutManager(new LinearLayoutManager(getContext()));
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("加载中...");
        if(getContext()instanceof WeatherActivity) {
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0)
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            view.findViewById(R.id.choose_area_fragmentLayout).setPadding(0, statusBarHeight, 0, 0);
        }
     return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button.setOnClickListener(v->{
            switch (level){
                case LEVEL_CITY:
                    RefreshProvinces();
                    break;
                case LEVEL_COUNTRY:
                    RefreshCities(chooseProvince);
                    break;
            }
        });
        RefreshProvinces();
        progressDialog.show();
        }

        private void RefreshProvinces(){
        level=LEVEL_PROVINCE;
        button.setVisibility(View.GONE);
        textView.setText("中国");
        progressDialog.show();
        new Thread(()->{
            CityAdapter adapter=new CityAdapter(CityUility.queryProvinces(),CityAdapter.TYPE_PROVINCE,(province->{
                chooseProvince=(Province) province;
                RefreshCities(chooseProvince);
            }));
            progressDialog.dismiss();
            if (level==LEVEL_PROVINCE)
                getActivity().runOnUiThread(()->cityList.setAdapter(adapter));
            }).start();
        }
        private void RefreshCities(Province province){
        level=LEVEL_CITY;
        button.setVisibility(View.VISIBLE);
        textView.setText(province.getProvinceName());
        progressDialog.show();
            new Thread(()->{
                CityAdapter adapter=new CityAdapter(CityUility.querCities(province),CityAdapter.TYPE_CITY,(city -> {
                    chooseCity =(City)city;
                    RefreshCounties(chooseCity);
                }));
                progressDialog.dismiss();
                if(level==LEVEL_CITY)
                    getActivity().runOnUiThread(()-> cityList.setAdapter(adapter));
            }).start();
        }
        private void RefreshCounties(City city) {
            level = LEVEL_COUNTRY;
            button.setVisibility(View.VISIBLE);
            textView.setText(city.getCityName());
            progressDialog.show();
            new Thread(() -> {
                CityAdapter adapter = new CityAdapter(CityUility.queryConties(city), CityAdapter.TYPE_COUNTRY, (county -> {
                    chooseCountry = (Country) county;
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getContext(), WeatherActivity.class);
                        intent.putExtra("weatherId", ((Country) county).getWeatherId());
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.weatherDrawer.closeDrawers();
                        activity.RefreshWeather(((Country) county).getWeatherId());
                    }
                }));
                progressDialog.dismiss();
                if (level == LEVEL_COUNTRY)
                    getActivity().runOnUiThread(() -> cityList.setAdapter(adapter));
            }).start();
        }
    }


