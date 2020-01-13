package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.example.weatherapp.WeatherInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainVPAdapter extends FragmentStatePagerAdapter {
    private Context mContext;
    private SharedPreferences sharedPreferences;

    List<String> favoriteLocations;

    public MainVPAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        this.sharedPreferences = context.getSharedPreferences("PREF_SHARED", 0);

        favoriteLocations = Arrays.asList(((this.sharedPreferences.getString("favorites", "")).split(";")));
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Gson gson = new Gson();
            String textLocation = this.sharedPreferences.getString("CURRENT_LOCATION", "");
            String json = this.sharedPreferences.getString(textLocation, "");
            WeatherInfo weatherData = gson.fromJson(json, WeatherInfo.class);

            CurrentDataFragment currentDataFragment = new CurrentDataFragment(weatherData, textLocation, position);
            return currentDataFragment;
        }
        else {
            Gson gson1 = new Gson();
            String json1 = this.sharedPreferences.getString(favoriteLocations.get(position-1), "");
            WeatherInfo weatherData1 = gson1.fromJson(json1, WeatherInfo.class);

            System.out.println(favoriteLocations.get(position-1));
            CurrentDataFragment currentDataFragment1 = new CurrentDataFragment(weatherData1, favoriteLocations.get(position-1), position);
            return currentDataFragment1;

        }
    }

    public void removeLocation() {
        favoriteLocations = Arrays.asList((this.sharedPreferences.getString("favorites", "")).split(";"));
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        if (favoriteLocations.get(0) == "") {
            return 1;
        }

        return favoriteLocations.size() + 1;
    }

}
