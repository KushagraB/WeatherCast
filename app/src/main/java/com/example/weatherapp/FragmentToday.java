package com.example.weatherapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentToday extends Fragment {
    private String textLocation;
    private SharedPreferences sharedPreferences;

    public FragmentToday(Context context, String textLocation) {
        // Required empty public constructor
        this.textLocation = textLocation;
        this.sharedPreferences = context.getSharedPreferences("PREF_SHARED", 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        Gson gson = new Gson();
        String json = this.sharedPreferences.getString(textLocation, "");
        WeatherInfo weatherData = gson.fromJson(json, WeatherInfo.class);

        TextView todayWindspeed = (TextView) view.findViewById(R.id.windspeed);
        TextView todayPressure = (TextView) view.findViewById(R.id.pressure);
        TextView todayPrecipitation = (TextView) view.findViewById(R.id.precipitation);
        TextView todayTemp = (TextView) view.findViewById(R.id.temperature);
        ImageView todayIcon = (ImageView) view.findViewById(R.id.icon5);
        TextView todaySummary = (TextView) view.findViewById(R.id.summary);
        TextView todayHumidity = (TextView) view.findViewById(R.id.humidity);
        TextView todayVisibility = (TextView) view.findViewById(R.id.visibility);
        TextView todayCloudCover = (TextView) view.findViewById(R.id.cloudcover);
        TextView todayOzone = (TextView) view.findViewById(R.id.ozone);

        todayWindspeed.setText(weatherData.windspeed + " mph");
        todayPressure.setText(weatherData.pressure + " mb");
        todayPrecipitation.setText(weatherData.precipitation + " mmph");
        todayTemp.setText(Math.round(Double.parseDouble(weatherData.temperature)) + "°F");
        todayIcon.setImageResource(weatherData.icon);

        if (weatherData.icon_string.equals("partly-cloudy-day")) {
            todaySummary.setText("cloudy day");
        }
        else if (weatherData.icon_string.equals("partly-cloudy-night")) {
            todaySummary.setText("cloudy night");
        }
        else {
            todaySummary.setText((weatherData.icon_string).replace("-", " "));
        }


        todayHumidity.setText(String.valueOf(Math.round((Double.parseDouble(weatherData.humidity))*100)) + "%");
        todayVisibility.setText(weatherData.visibility + " km");
        todayCloudCover.setText(weatherData.cloudCover + "%");
        todayOzone.setText(weatherData.ozone + " DU");

        return view;
    }

}
