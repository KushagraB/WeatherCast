package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    String strdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        strdata = null;
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();

        /* Obtain String from Intent  */
        if(intent !=null) {
            strdata = intent.getExtras().getString("loc");
        }

        getSupportActionBar().setTitle(strdata);

        ViewPager viewPager = findViewById(R.id.detailviewpager);
        TabLayout tabLayout = findViewById(R.id.detailtablayout);

        DetailActivityVPAdapter viewPagerAdapter = new DetailActivityVPAdapter(this, getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentToday(getApplicationContext(),strdata));
        viewPagerAdapter.addFragment(new FragmentChart(getApplicationContext(),strdata));
        viewPagerAdapter.addFragment(new FragmentPictures(strdata));

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.calendar_today);
        tabLayout.getTabAt(1).setIcon(R.drawable.trending_up);
        tabLayout.getTabAt(2).setIcon(R.drawable.google_photos);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tweet_menu, menu);
        MenuItem tweet = menu.findItem(R.id.tweetFromHere);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PREF_SHARED", 0);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(strdata, "");
        final WeatherInfo weatherData = gson.fromJson(json, WeatherInfo.class);


        tweet.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, "This is a Test.");
                tweetIntent.setType("text/plain");

                PackageManager packManager = getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

                boolean resolved = false;
                for (ResolveInfo resolveInfo : resolvedInfoList) {
                    if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                        tweetIntent.setClassName(
                                resolveInfo.activityInfo.packageName,
                                resolveInfo.activityInfo.name);
                        resolved = true;
                        break;
                    }
                }
                if (resolved) {
                    startActivity(tweetIntent);
                } else {
                    String message = "Check Out " + strdata +  "'s Weather! It is " + Math.round(Double.parseDouble(weatherData.temperature)) + "°F! #CSCI571WeatherSearch";
                    Intent i = new Intent();
                    i.putExtra(Intent.EXTRA_TEXT, message);
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + message));
                    startActivity(i);
//                    Toast.makeText(this, "Twitter app isn't found", Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


}
