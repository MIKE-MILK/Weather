package com.example.weather.util;

import com.example.weather.db.City;
import com.example.weather.db.Country;
import com.example.weather.db.Province;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;

public class CityUility {

    private static final String TAG="CityUtility";
    private static final String CITY_API_URL="http://guolin.tech/api/china/";

    public static List<Province>queryProvinces(){
        List<Province>provinces= LitePal.findAll(Province.class);
        if (provinces.isEmpty()){
            try {
                String json=HttpUtility.sendRequest(CITY_API_URL);
                if (json==null||json.isEmpty()){
                    LogUtility.e(TAG,"获取省份信息失败json=empty");
                    return null;
                }
                JSONArray array=new JSONArray(json);
                for (int i=0;i<array.length();i++){
                    Province province=new Province();
                    JSONObject object=array.getJSONObject(i);
                    province.setProvinceCode(object.getInt("id"));
                    province.setProvinceName(object.getString("name"));
                    province.save();
                    provinces.add(province);
                }
            }catch (Exception e){
                LogUtility.e(TAG,e.toString());
            }
        }
        return provinces;
    }

    public static List<City>querCities(Province province){
        List<City>cities=LitePal.where("ProvinceCode="+province.getProvinceCode()).find(City.class);
        if (cities.isEmpty()){
            try {
                String json=HttpUtility.sendRequest(CITY_API_URL+"/"+province.getProvinceCode());
                if (json==null||json.isEmpty()){
                    LogUtility.e(TAG,"获取城市信息错误 Province : id = "+province.getProvinceCode()+" name = "+province.getProvinceName());
                    return null;
                }
                JSONArray array=new JSONArray(json);
                for (int i=0;i<array.length();i++){
                    City city=new City();
                    JSONObject object=array.getJSONObject(i);
                    city.setCityCode(object.getInt("id"));
                    city.setCityName(object.getString("name"));
                    city.setProvinceId(province.getProvinceCode());
                    city.save();
                    cities.add(city);
                }
            }catch (Exception e){
                LogUtility.e(TAG,e.toString());
            }
        }
        return cities;
    }
    public static List<Country>queryConties(City city){
        List<Country>countries=LitePal.where("cityCode"+city.getCityCode()).find(Country.class);
        if (countries.isEmpty()){
            try {
                String json=HttpUtility.sendRequest(CITY_API_URL+"/"+city.getCityCode());
                if (json==null||json.isEmpty()){
                    LogUtility.e(TAG,"获取县城信息失败 City:id="+city.getProvinceId()+"name="+city.getCityName());
                    return null;
                }
                JSONArray array=new JSONArray(json);
                for (int i=0;i<array.length();i++){
                    Country country=new Country();
                    JSONObject object=array.getJSONObject(i);
                    country.setCountryName(object.getString("name"));
                    country.setCountryId(object.getInt("id"));
                    country.save();
                    countries.add(country);
                }
            }catch (Exception e){
                LogUtility.e(TAG,e.toString());
            }
        }
        return countries;
    }
}
