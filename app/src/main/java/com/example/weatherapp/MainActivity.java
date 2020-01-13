package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FrameLayout progressBarLayout;
    MainVPAdapter viewPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    private SearchView searchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private SearchAdapter searchAdapter;
    private Handler handler;
    private String visibleLocation;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        progressBarLayout = findViewById(R.id.progressSymbol);
        progressBarLayout.setVisibility(View.VISIBLE);
        searchAdapter = new SearchAdapter(this,
                android.R.layout.simple_dropdown_item_1line);

        viewPager = findViewById(R.id.mainviewpager);
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        String[] coordinates = getPhoneGPSLocation();
        getLocationAddress(coordinates);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setBackgroundColor(getResources().getColor(R.color.searchBarColor));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.white));
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.holo_blue_light);
        searchAutoComplete.setAdapter(searchAdapter);
        searchAutoComplete.setThreshold(1);

        searchView.setOnQueryTextListener(
                new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url ="http://kushagra-weather-app.us-east-2.elasticbeanstalk.com/getPlaces/" + searchAutoComplete.getText().toString();
                        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                                Request.Method.GET,
                                url,
                                null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        List<String> stringList = new ArrayList<>();
                                        for(int i = 0; i < response.length(); i++) {
                                            try {
                                                stringList.add(response.getString(i));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        searchAdapter.setData(stringList);
                                        searchAdapter.notifyDataSetChanged();
                                    }
                                },
                                new Response.ErrorListener(){
                                    @Override
                                    public void onErrorResponse(VolleyError error){
                                        // Do something when error occurred

                                    }
                                }
                        );


                        queue.add(jsonArrayRequest);
                        return false;
                    }
                }
        );

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                Intent passSearchQuery = new Intent(getApplicationContext(), FragmentSearchResults.class);
                passSearchQuery.putExtra("query", String.valueOf(adapterView.getItemAtPosition(itemIndex)));
                startActivity(passSearchQuery);

            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void remove() {
        viewPagerAdapter.removeLocation();
        viewPager.setCurrentItem(0);
    }

    public void storeCurrentLocationInfo(final String[] address, final String textLocation){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://kushagra-weather-app.us-east-2.elasticbeanstalk.com/findWeatherData/" + address[0] + "/" + address[1];

        System.out.println(url);

        JsonObjectRequest locationDataRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        sharedPreferences = getApplicationContext().getSharedPreferences("PREF_SHARED", getApplicationContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("CURRENT_LOCATION", textLocation);

                        WeatherInfo weatherData = null;
                        try {
                            weatherData = new WeatherInfo(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Gson gson = new Gson();
                        String json = gson.toJson(weatherData);
                        editor.putString(textLocation,json);
                        editor.apply();

                        progressBarLayout.setVisibility(View.INVISIBLE);

                        viewPagerAdapter = new MainVPAdapter(getApplicationContext(), getSupportFragmentManager());
                        viewPager.setAdapter(viewPagerAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        queue.add(locationDataRequest);
    }

    public void getLocationAddress(final String[] coordinates){

        RequestQueue queue = Volley.newRequestQueue(this);
        String mapsRequest ="https://maps.googleapis.com/maps/api/geocode/json?latlng=" + coordinates[0] + "," + coordinates[1] + "&key=AIzaSyB0j7FdlE3ifPNzANfcam1QsZ4pfuiKVok";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, mapsRequest,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject locationAddressJSON = response.getJSONObject("plus_code");
                            String[] locationAddress = (locationAddressJSON.getString("compound_code")).split(",");
                            String[] city = locationAddress[0].split(" ");
                            locationAddress[0] = city[1] + " " + city[2];
                            StringBuilder textLocation = new StringBuilder();

                            for(int i = 0; i < locationAddress.length; i++){
                                if (i != locationAddress.length-1) {
                                    textLocation.append(locationAddress[i] + ",");
                                }
                                else {
                                    textLocation.append(locationAddress[i]);
                                }
                            }

                            visibleLocation = textLocation.toString();
                            storeCurrentLocationInfo(coordinates, textLocation.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(jsonRequest);
    }

    public String[] getPhoneGPSLocation() {
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setCostAllowed(false);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        String providerName = locationManager.getBestProvider(criteria, true);

        Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String[] output = { Double.toString(gpsLocation.getLatitude()), Double.toString(gpsLocation.getLongitude()) };
        return output;
    }
}

class WeatherInfo {

    Map<String,Integer> iconMap;
    JSONObject response;
    String temperature, windspeed, summary, icon_string, weeklySummary, humidity, precipitation, cloudCover, visibility, ozone, pressure;
    int icon, dailyIcon;
    List<Map<String, String>> dailyInfo = new ArrayList<>();

    public WeatherInfo(JSONObject jsonData) throws JSONException {
        this.response = jsonData;

        iconMap = new HashMap<>();
        iconMap.put("partly-cloudy-day", R.drawable.weather_partly_cloudy);
        iconMap.put("partly-cloudy-night", R.drawable.weather_night_partly_cloudy);
        iconMap.put("cloudy",R.drawable.weather_cloudy);
        iconMap.put("wind", R.drawable.weather_windy_copy);
        iconMap.put("fog",R.drawable.weather_fog);
        iconMap.put("sleet",R.drawable.weather_snowy_rainy);
        iconMap.put("snow",R.drawable.weather_snowy);
        iconMap.put("rain", R.drawable.weather_rainy);
        iconMap.put("clear-night",R.drawable.weather_night);
        iconMap.put("clear-day", R.drawable.weather_sunny);

        this.temperature = String.format("%.2f", Double.parseDouble(jsonData.getJSONObject("currently").getString("temperature")));
        this.icon = iconMap.get(jsonData.getJSONObject("currently").getString("icon"));
        this.icon_string = jsonData.getJSONObject("currently").getString("icon");
        this.windspeed = String.format("%.2f", Double.parseDouble(jsonData.getJSONObject("currently").getString("windSpeed")));
        this.summary = jsonData.getJSONObject("currently").getString("summary");
        this.weeklySummary = jsonData.getJSONObject("daily").getString("summary");
        this.humidity = jsonData.getJSONObject("currently").getString("humidity");

        try {
            this.dailyIcon = iconMap.get(jsonData.getJSONObject("daily").getString("icon"));
        }
        catch (Exception exception){
            this.dailyIcon = R.drawable.weather_sunny;
        }

        this.precipitation = String.format("%.2f", Double.parseDouble(jsonData.getJSONObject("currently").getString("precipIntensity")));
        this.cloudCover = jsonData.getJSONObject("currently").getString("precipIntensity");
        this.visibility = String.format("%.2f", Double.parseDouble(jsonData.getJSONObject("currently").getString("visibility")));
        this.ozone = String.format("%.2f", Double.parseDouble(jsonData.getJSONObject("currently").getString("ozone")));
        this.pressure = String.format("%.2f", Double.parseDouble(jsonData.getJSONObject("currently").getString("pressure")));

        JSONArray dailyData = jsonData.getJSONObject("daily").getJSONArray("data");
        String date, lowTemp, highTemp;
        Date timestamp;
        int dailyicon;

        for(int i = 0; i < dailyData.length(); i++)
        {
            Map<String, String> info = new HashMap<>();
            dailyicon = iconMap.get(dailyData.getJSONObject(i).getString("icon"));
            lowTemp = dailyData.getJSONObject(i).getString("temperatureMin");
            highTemp = dailyData.getJSONObject(i).getString("temperatureMax");

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            timestamp = new Date(Long.parseLong(dailyData.getJSONObject(i).getString("time")) * 1000);
            date = dateFormat.format(timestamp);

            info.put("timestamp", date);
            info.put("highTemp", highTemp);
            info.put("lowTemp", lowTemp);
            info.put("dailyicon", String.valueOf(dailyicon));
            dailyInfo.add(info);
        }
    }
}

class SearchAdapter extends ArrayAdapter<String>{
    private List<String> data;

    public SearchAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        data = new ArrayList<>();
    }

    @Override
    public View getView(int position, View cView, ViewGroup parent) {

        TextView view = new TextView(getContext());
        view.setPadding(20,65,10,65);
        view.setText(data.get(position));
        view.setTextSize(16);
        view.setBackgroundColor(Color.WHITE);
        view.setTextColor(Color.BLACK);
        return view;
    }

    public void setData(List<String> list) {
        data.clear();
        for (int i =0; i < list.size(); i++) {
            data.add(list.get(i));
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return data.get(position);
    }
}
