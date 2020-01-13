package com.example.weatherapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
import org.w3c.dom.Text;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.WeatherInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentDataFragment extends Fragment {

    String textLocation, temperature, windspeed, summary, weekly_card_summary, humidity, precipitation, cloudCover, visibility, ozone, pressure;
    int icon, position, dailyIcon;
    List<Map<String,String>> dailyInfo;
    FloatingActionButton fab;
    MainActivity mainActivity;

    public CurrentDataFragment(WeatherInfo weatherData, String textLocation, Integer position) {
        // Required empty public constructor
        System.out.println(weatherData);
        this.temperature = weatherData.temperature;
        this.windspeed = weatherData.windspeed;
        this.summary = weatherData.summary;
        this.icon = weatherData.icon;
        this.visibility = weatherData.visibility;
        this.pressure = weatherData.pressure;
        this.humidity = weatherData.humidity;
        this.textLocation = textLocation;
        this.position = position;
        this.dailyInfo = weatherData.dailyInfo;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_data, container, false);
        fab = view.findViewById(R.id.fab);
        if (position == 0) {
            fab.hide();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavoriteButton();
            }
        });

        TextView currentCardTemp = (TextView) view.findViewById(R.id.temperature);
        TextView currentCardSummary = (TextView) view.findViewById(R.id.summary);
        TextView currentLocation = (TextView) view.findViewById(R.id.currentLocation);
        ImageView currentPageIcon = (ImageView) view.findViewById(R.id.icon);

        TextView currentCardHumidity = (TextView) view.findViewById(R.id.humidity);
        TextView currentCardWindspeed = (TextView) view.findViewById(R.id.windspeed);
        TextView currentCardVisibility = (TextView) view.findViewById(R.id.visibility);
        TextView currentCardPressure = (TextView) view.findViewById(R.id.pressure);

        TextView[] date = new TextView[8];
        ImageView[] icon = new ImageView[8];
        TextView[] lowTemp = new TextView[8];
        TextView[] highTemp = new TextView[8];

        for (int i=1; i < 9; i++) {
            date[i-1] = view.findViewById(getResources().getIdentifier("date" + i, "id",
                    getActivity().getPackageName()));
            icon[i-1] = view.findViewById(getResources().getIdentifier("icon" + i, "id",
                    getActivity().getPackageName()));
            lowTemp[i-1] = view.findViewById(getResources().getIdentifier("lowtemp" + i, "id",
                    getActivity().getPackageName()));
            highTemp[i-1] = view.findViewById(getResources().getIdentifier("hightemp" + i, "id",
                    getActivity().getPackageName()));

            date[i-1].setText((this.dailyInfo.get(i-1)).get("timestamp"));
            System.out.println((this.dailyInfo.get(i-1)).get("dailyicon"));
            icon[i-1].setImageResource(Integer.parseInt((this.dailyInfo.get(i-1)).get("dailyicon")));
            lowTemp[i-1].setText(String.valueOf(Math.round(Double.parseDouble(this.dailyInfo.get(i-1).get("lowTemp")))));
            highTemp[i-1].setText(String.valueOf(Math.round(Double.parseDouble(this.dailyInfo.get(i-1).get("highTemp")))));

        }

        currentCardTemp.setText(Math.round(Double.parseDouble(this.temperature)) + "°F");
        currentCardSummary.setText(this.summary);
        currentLocation.setText(this.textLocation);
        currentPageIcon.setImageResource(this.icon);

        currentCardHumidity.setText(String.valueOf(Math.round((Double.parseDouble(this.humidity))*100)) + "%");
        currentCardWindspeed.setText(this.windspeed + " mph");
        currentCardVisibility.setText(this.visibility + " km");
        currentCardPressure.setText(this.pressure + " mb");

        CardView cardView = view.findViewById(R.id.detailViewCard);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("loc", textLocation);
                startActivity(intent);
            }
        });

//        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
//        setLocation(view, pref);

        return view;
    }

    public void toggleFavoriteButton() {
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("PREF_SHARED", MODE_PRIVATE);
        Toast.makeText(getActivity().getApplicationContext(), this.textLocation + " is removed from your favorites", Toast.LENGTH_LONG).show();

        String favorites = sharedPreferences.getString("favorites","");
        favorites = favorites.replace(this.textLocation + ";", "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("favorites", favorites);
        editor.apply();

        MainActivity i = (MainActivity) getActivity();
        i.remove();

    }

    public void setLocation(View view, final SharedPreferences pref) {

        // Instantiate the RequestQueue.
//        System.out.println(Environment.getDataDirectory());

        final TextView currentLocation = view.findViewById(R.id.currentLocation);
        RequestQueue queue;
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        if (pref.getString("currentLocation", null) != null) {
            System.out.println("Already there");
            JSONObject reader = null;
            try {
                reader = new JSONObject(pref.getString("currentLocation", null));
                String location_string = reader.getString("loc");
                currentLocation.setText(location_string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String url = "http://www.ip-api.com/json/";

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            JSONObject reader = null;
                            try {
                                reader = new JSONObject(response);
                                String location_string = reader.getString("city") + ", " + reader.getString("region") + ", " + reader.getString("countryCode");
                                currentLocation.setText(location_string);

                                SharedPreferences.Editor editor = pref.edit();
                                JSONObject coordinates = new JSONObject("{'latitude': " + reader.getString("lat") + ", 'longitude': " + reader.getString("lon") + ", 'loc': " + location_string + "}");
                                editor.putString("currentLocation", coordinates.toString());
                                editor.commit();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    currentLocation.setText("That didn't work!");
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }


    }
}


