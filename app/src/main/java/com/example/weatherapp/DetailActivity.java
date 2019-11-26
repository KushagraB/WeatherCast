package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Set LOCATION here");

        ViewPager viewPager = findViewById(R.id.detailviewpager);
        TabLayout tabLayout = findViewById(R.id.detailtablayout);

        DetailActivityVPAdapter viewPagerAdapter = new DetailActivityVPAdapter(this, getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentToday());
        viewPagerAdapter.addFragment(new FragmentChart());
        viewPagerAdapter.addFragment(new FragmentPictures());

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.calendar_today);
        tabLayout.getTabAt(1).setIcon(R.drawable.trending_up);
        tabLayout.getTabAt(2).setIcon(R.drawable.google_photos);



    }

}
