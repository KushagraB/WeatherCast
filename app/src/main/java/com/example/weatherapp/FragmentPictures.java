package com.example.weatherapp;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPictures extends Fragment {


    public FragmentPictures() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        List<String> images = new ArrayList<>();
        images.add("https://www.loewshotels.com/blog/wp-content/uploads/2018/07/santa-monica-pier_t20_0A466V-700x700.jpg");
        images.add("https://www.loewshotels.com/blog/wp-content/uploads/2018/07/santa-monica-pier_t20_0A466V-700x700.jpg");
        images.add("https://www.loewshotels.com/blog/wp-content/uploads/2018/07/santa-monica-pier_t20_0A466V-700x700.jpg");
        images.add("https://www.loewshotels.com/blog/wp-content/uploads/2018/07/santa-monica-pier_t20_0A466V-700x700.jpg");
        images.add("https://www.loewshotels.com/blog/wp-content/uploads/2018/07/santa-monica-pier_t20_0A466V-700x700.jpg");
        images.add("https://www.loewshotels.com/blog/wp-content/uploads/2018/07/santa-monica-pier_t20_0A466V-700x700.jpg");
        images.add("https://www.loewshotels.com/blog/wp-content/uploads/2018/07/santa-monica-pier_t20_0A466V-700x700.jpg");
        images.add("https://www.loewshotels.com/blog/wp-content/uploads/2018/07/santa-monica-pier_t20_0A466V-700x700.jpg");

        RVAdapter rvAdapter = new RVAdapter(getActivity(), images);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvAdapter);

        return view;
    }

}
