package com.example.mars;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MarsWeather extends Application {
    private RequestQueue requestQueue;
    private static MarsWeather marsWeather;
    public static final String TAG=MarsWeather.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        marsWeather=this;
        requestQueue= Volley.newRequestQueue(getApplicationContext());
    }

    public static synchronized MarsWeather getMarsWeather() {
        return marsWeather;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public <T> void add(Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancel(){
        requestQueue.cancelAll(TAG);
    }
}
