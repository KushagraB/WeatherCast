package com.example.weatherapp;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetailActivityVPAdapter extends FragmentStatePagerAdapter {
        private Context mContext;
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public DetailActivityVPAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FragmentToday fragmentToday = new FragmentToday();
                    return fragmentToday;
                case 1:
                    FragmentChart fragmentChart = new FragmentChart();
                    return fragmentChart;
                case 2:
                    FragmentPictures fragmentPictures = new FragmentPictures();
                    return fragmentPictures;
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "TODAY";
                case 1:
                    return "WEEKLY";
                case 2:
                    return "PHOTOS";
                default:
                    return null;

            }
        }

}
