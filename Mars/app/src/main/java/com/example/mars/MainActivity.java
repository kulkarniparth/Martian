package com.example.mars;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity<x> extends AppCompatActivity {

    ImageView image;
    TextView temperature,atmosphere,problem,roverInfo,solInfo;
    MarsWeather marsWeather = MarsWeather.getMarsWeather();
    public static final String url="https://api.maas2.jiinxt.com/";
    public static final String photourl="https://api.nasa.gov/mars-photos/api/v1/rovers/";
    int x=new Random().nextInt(3);
    private String rovername;

    public static final String part="/photos?sol=";
    public static final String added="&api_key=";
    public static final String api_key="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image=(ImageView)findViewById(R.id.photo);
        problem=(TextView)findViewById(R.id.error);
        temperature=(TextView)findViewById(R.id.temperature);
        atmosphere=(TextView)findViewById(R.id.atmosphere);
        roverInfo=(TextView)findViewById(R.id.rover);
        solInfo=(TextView)findViewById(R.id.sol);

        temperature.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Lato-Light.ttf"));
        atmosphere.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Lato-Light.ttf"));
        roverInfo.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Lato-Light.ttf"));
        solInfo.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Lato-Light.ttf"));

        if(x==0)
            rovername="Opportunity";
        else if(x==1)
            rovername="Curiosity";
        else
            rovername="Spirit";

        loadWeather();
        loadPhoto();
    }

    private void loadWeather() {
        CustomJSONRequest customJSONRequest = new CustomJSONRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String minTemp,maxTemp,atmos,sol,season;

                minTemp=response.optString("min_temp");
                maxTemp=response.optString("max_temp");
                atmos=response.optString("atmo_opacity");
                sol=response.optString("sol");
                season=response.optString("season");
                temperature.setText(minTemp+"°"+"    "+maxTemp+"°");
                //atmosphere.setText(atmos);
                solInfo.setText("Sol"+sol+","+season);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                problem.setVisibility(View.VISIBLE);
                problem.setText("Oops! Seems the cloud are in our way...");
                problem.setTextColor(Color.WHITE);
                error.printStackTrace();
            }
        });
        customJSONRequest.setPriority(Request.Priority.HIGH);
        marsWeather.add(customJSONRequest);

    }


    private void loadPhoto() {
        CustomJSONRequest customJSONRequest = new CustomJSONRequest(Request.Method.GET,photourl + "curiosity"+part+"1000"+ added + api_key, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray array = response.getJSONArray("photos");
                    JSONObject object = array.getJSONObject(new Random().nextInt(array.length()));
                    String imageUrl = object.getString("img_src");
                    JSONObject rover = object.getJSONObject("rover");
                    //String rovername = rover.getString("name");
                    roverInfo.setText(rovername+" Rover");
                    //Picasso.get().load(imageUrl).centerCrop().resize(40,50).into(image);
                    //imageUrl="https://mars.jpl.nasa.gov/msl-raw-images/msss/01000/mcam/1000MR0044631130503673C00_DXXX.jpg";
                    System.out.println(imageUrl);
                    loadImage(imageUrl);
                } catch (JSONException e) {
                    ImageError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ImageError(error);
            }
        });

        customJSONRequest.setPriority(Request.Priority.HIGH);
        marsWeather.add(customJSONRequest);
    }

    private void loadImage(String imageUrl) {
        ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                image.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Here");
                ImageError(error);
            }
        });
        marsWeather.add(imageRequest);
    }

    private void ImageError(VolleyError error) {
        int color=Color.parseColor("#FF7522");
        image.setBackgroundColor(color);
        error.printStackTrace();
    }

    private void ImageError(JSONException e) {
        int color=Color.parseColor("#FF7522");
        image.setBackgroundColor(color);
        e.printStackTrace();
    }
}
