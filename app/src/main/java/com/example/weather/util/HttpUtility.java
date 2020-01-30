package com.example.weather.util;

import com.bumptech.glide.manager.RequestManagerRetriever;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtility {
    private static String TAG="httpUtility";

    public interface  OnResponseListener{
        void onResponse(String response);
    }

    public static void SendRequest(String address,OnResponseListener onResponseListener){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onResponseListener.onResponse(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onResponseListener.onResponse(response.body().string());
            }
        });
    }
    public static String sendRequest(String address)throws IOException{
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        return client.newCall(request).execute().body().string();
    }
}
