package com.example.weatherapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentDataFragment extends Fragment {

    public static SharedPreferences pref; // 0 - for private mode

    public CurrentDataFragment() {
        // Required empty public constructor
        this.pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_data, container, false);


        CardView cardView = view.findViewById(R.id.detailViewCard);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                startActivity(intent);
            }
        });

//        if (this.pref.getString("currentLocation", null) != null) {
//            System.out.println("Already there");
//        }
//        else {
            System.out.println("Created Shared Preferences!");
            setLocation(view);
//        }

        return view;
    }

    public void setLocation(View view) {
        final TextView textView = view.findViewById(R.id.temperature);
        final TextView currentLocation = view.findViewById(R.id.currentLocation);

        // Instantiate the RequestQueue.
//        System.out.println(Environment.getDataDirectory());

        RequestQueue queue;
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url ="http://www.ip-api.com/json/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        JSONObject reader = null;
                        try {
                            reader = new JSONObject(response);
                            currentLocation.setText(reader.getString("city") + ", " + reader.getString("region") + ", " + reader.getString("countryCode"));

//                            SharedPreferences.Editor editor = CurrentDataFragment.pref.edit();
//                            try {
//                                JSONObject coordinates = new JSONObject("{'latitude': " + reader.getString("lat") + ", 'longitude': " + reader.getString("lon"));
//                                editor.putString("currentLocation", coordinates.toString());
//                                editor.commit();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
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


