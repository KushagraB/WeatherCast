package com.example.weatherapp;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MainVPAdapter extends FragmentStatePagerAdapter {
    private Context mContext;

    public MainVPAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CurrentDataFragment currentDataFragment = new CurrentDataFragment();
                return currentDataFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

}
