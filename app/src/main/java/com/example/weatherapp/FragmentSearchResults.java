package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FragmentSearchResults extends AppCompatActivity {
    private String textLocation;
    private SharedPreferences sharedPreferences;
    FrameLayout progressBarLayout;
    LinearLayout searchResultsPage;
    FloatingActionButton fab;
    Boolean isLocationFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getApplicationContext().getSharedPreferences("PREF_SHARED", getApplicationContext().MODE_PRIVATE);

        Intent intent = getIntent();
        textLocation = intent.getStringExtra("query");
        getSupportActionBar().setTitle(textLocation);

        progressBarLayout = findViewById(R.id.progressSymbol);
        progressBarLayout.setVisibility(View.VISIBLE);

        searchResultsPage = findViewById(R.id.searchResultsFragment);
        searchResultsPage.setVisibility(View.INVISIBLE);

        fab = findViewById(R.id.fab);

        final String locations = this.sharedPreferences.getString("favorites","");
        isLocationFavorite = Arrays.asList(locations.split(";")).contains(textLocation);
        if (isLocationFavorite) {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.map_marker_minus));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavoriteButton();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kushagra-weather-app.us-east-2.elasticbeanstalk.com/findLatLong/" + textLocation;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        String latitude = "";
                        String longitude = "";
                        try {
                            latitude = response.getString("lat");
                            longitude = response.getString("lng");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        storeCurrentLocationInfo(latitude, longitude, textLocation);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("RESPONSE","MESSAGE");
            }
        });

        queue.add(jsonRequest);
//        getDataFromSharedPreferences();

    }

    public void storeCurrentLocationInfo(String latitude, String longitude, final String textLocation){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kushagra-weather-app.us-east-2.elasticbeanstalk.com/findWeatherData/" + latitude + "/" + longitude;

        System.out.println(url);

        JsonObjectRequest locationDataRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        WeatherInfo weatherInfo = null;
                        try {
                            weatherInfo = new WeatherInfo(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Gson gson = new Gson();
                        String json = gson.toJson(weatherInfo);
                        editor.putString(textLocation,json);
                        editor.apply();

                        getDataFromSharedPreferences();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        queue.add(locationDataRequest);
    }

    public void toggleFavoriteButton() {

        if(isLocationFavorite) {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.map_marker_plus));
            Toast.makeText(FragmentSearchResults.this, textLocation + " is removed from your favorites", Toast.LENGTH_LONG).show();

            String favorites = this.sharedPreferences.getString("favorites","");
            favorites = favorites.replace(textLocation + ";", "");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("favorites", favorites);
            editor.apply();
        }
        else {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.map_marker_minus));
            Toast.makeText(FragmentSearchResults.this, textLocation + " is added to your favorites", Toast.LENGTH_LONG).show();

            String favorites = this.sharedPreferences.getString("favorites","");
            favorites += textLocation + ";";

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("favorites", favorites);
            editor.apply();
        }

        isLocationFavorite = !isLocationFavorite;
    }

    public void getDataFromSharedPreferences() {
        Gson gson = new Gson();
        String json = this.sharedPreferences.getString(textLocation, "");
        WeatherInfo weatherInfo = gson.fromJson(json, WeatherInfo.class);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(textLocation);

        progressBarLayout.setVisibility(View.INVISIBLE);
        searchResultsPage.setVisibility(View.VISIBLE);

        TextView currentCardTemp = (TextView) findViewById(R.id.temperature);
        TextView currentCardSummary = (TextView) findViewById(R.id.summary);
        TextView currentLocation = (TextView) findViewById(R.id.currentLocation);
        ImageView currentPageIcon = (ImageView) findViewById(R.id.icon);

        TextView currentCardHumidity = (TextView) findViewById(R.id.humidity);
        TextView currentCardWindspeed = (TextView) findViewById(R.id.windspeed);
        TextView currentCardVisibility = (TextView) findViewById(R.id.visibility);
        TextView currentCardPressure = (TextView) findViewById(R.id.pressure);

        TextView[] date = new TextView[8];
        ImageView[] icon = new ImageView[8];
        TextView[] lowTemp = new TextView[8];
        TextView[] highTemp = new TextView[8];

        for (int i=1; i < 9; i++) {
            date[i-1] = findViewById(getResources().getIdentifier("date" + i, "id",
                    getPackageName()));
            icon[i-1] = findViewById(getResources().getIdentifier("icon" + i, "id",
                    getPackageName()));
            lowTemp[i-1] = findViewById(getResources().getIdentifier("lowtemp" + i, "id",
                    getPackageName()));
            highTemp[i-1] = findViewById(getResources().getIdentifier("hightemp" + i, "id",
                    getPackageName()));

            date[i-1].setText((weatherInfo.dailyInfo.get(i-1)).get("timestamp"));
            System.out.println((weatherInfo.dailyInfo.get(i-1)).get("dailyicon"));
            icon[i-1].setImageResource(Integer.parseInt((weatherInfo.dailyInfo.get(i-1)).get("dailyicon")));
            lowTemp[i-1].setText(String.valueOf(Math.round(Double.parseDouble(weatherInfo.dailyInfo.get(i-1).get("lowTemp")))));
            highTemp[i-1].setText(String.valueOf(Math.round(Double.parseDouble(weatherInfo.dailyInfo.get(i-1).get("highTemp")))));

        }

        currentCardTemp.setText(Math.round(Double.parseDouble(weatherInfo.temperature)) + "°F");
        currentCardSummary.setText(weatherInfo.summary);
        currentLocation.setText(textLocation);
        currentPageIcon.setImageResource(weatherInfo.icon);

        currentCardHumidity.setText(String.valueOf(Math.round((Double.parseDouble(weatherInfo.humidity))*100)) + "%");
        currentCardWindspeed.setText(weatherInfo.windspeed + " mph");
        currentCardVisibility.setText(weatherInfo.visibility + " km");
        currentCardPressure.setText(weatherInfo.pressure + " mb");

        CardView cardView = findViewById(R.id.detailViewCard);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentSearchResults.this, DetailActivity.class);
                intent.putExtra("loc", textLocation);
                startActivity(intent);
            }
        });
    }
}
