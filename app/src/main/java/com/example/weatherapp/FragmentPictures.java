package com.example.weatherapp;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPictures extends Fragment {
    private String textLocation;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter rvAdapter;
    FrameLayout progressBarLayout;

    public FragmentPictures(String textLocation) {
        // Required empty public constructor
        this.textLocation = textLocation;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);

        progressBarLayout = view.findViewById(R.id.progressSymbol);
        progressBarLayout.setVisibility(View.VISIBLE);

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        String url = "https://www.googleapis.com/customsearch/v1?q=" + textLocation + "&cx=009952109813749107411:b6dkcsrnwb9&imgSize=huge&imgType=news&num=10&searchType=image&key=AIzaSyB0j7FdlE3ifPNzANfcam1QsZ4pfuiKVok";
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            List<String> images = new ArrayList<>();

                            JSONArray items = response.getJSONArray("items");
                            for(int i=0,j=0; i < 10 && j < 8; i++){

                                String link = items.getJSONObject(i).getString("link");
                                if( !link.endsWith(".svg") && !link.endsWith(".png")){
                                    images.add(link);
                                    j += 1;
                                }
                            }

                            progressBarLayout.setVisibility(View.INVISIBLE);
                            rvAdapter = new RVAdapter(getContext(), images);
                            recyclerView.setAdapter(rvAdapter);

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
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(rvAdapter);

        return view;
    }

}
