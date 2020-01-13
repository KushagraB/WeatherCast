package com.example.weatherapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChart extends Fragment {
    private String textLocation;
    private SharedPreferences sharedPreferences;

    public FragmentChart(Context context, String textLocation) {
        // Required empty public constructor
        this.textLocation = textLocation;
        this.sharedPreferences = context.getSharedPreferences("PREF_SHARED", 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        Gson gson = new Gson();
        String json = this.sharedPreferences.getString(textLocation, "");
        WeatherInfo weatherData = gson.fromJson(json, WeatherInfo.class);

        ImageView icon = view.findViewById(R.id.icon);
        TextView weeklySummary = view.findViewById(R.id.weekly_summary);

        icon.setImageResource(weatherData.dailyIcon);
        weeklySummary.setText(weatherData.weeklySummary);

        LineChart chart = view.findViewById(R.id.linechart);
        
        ArrayList<Entry> temperatureMin = new ArrayList<>();
        ArrayList<Entry> temperatureMax = new ArrayList<>();
        ArrayList<ILineDataSet> lineData = new ArrayList<>();
        
        for (int i=0; i < (weatherData.dailyInfo).size(); i++) {
            temperatureMin.add(new Entry((float) i, Float.parseFloat((weatherData.dailyInfo).get(i).get("lowTemp"))));
            temperatureMax.add(new Entry((float) i, Float.parseFloat((weatherData.dailyInfo).get(i).get("highTemp"))));
        }
        
        LineDataSet tempMinLine =  new LineDataSet(temperatureMin, "Temperature Min");
        tempMinLine.setColor(Color.parseColor("#B075F4"));
        lineData.add(tempMinLine);

        LineDataSet tempMaxLine =  new LineDataSet(temperatureMax, "Temperature Max");
        tempMaxLine.setColor(Color.parseColor("#DF9108"));
        lineData.add(tempMaxLine);

        chart.getLegend().setTextColor(getResources().getColor(R.color.white));
        LineData data = new LineData(lineData);
        chart.getXAxis().setTextColor(getResources().getColor(R.color.white));
        chart.getAxisLeft().setTextColor(getResources().getColor(R.color.white));
        chart.getAxisRight().setTextColor(getResources().getColor(R.color.white));
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisRight().setDrawGridLines(true);

        List<LegendEntry> legends = new ArrayList<>();
        Legend legend = chart.getLegend();
        legend.setTextSize(15);

        LegendEntry minTemp = new LegendEntry();
        LegendEntry maxTemp = new LegendEntry();

        minTemp.label = "Minimum Temperature";
        maxTemp.label = "Maximum Temperature";

        minTemp.formColor = getResources().getColor(R.color.magenta);
        maxTemp.formColor = getResources().getColor(R.color.yellow);

        minTemp.formSize = 18;
        maxTemp.formSize = 18;


        legends.add(minTemp);
        legends.add(maxTemp);

        legend.setForm(Legend.LegendForm.SQUARE);
        chart.getLegend().setCustom(legends);
        chart.setData(data);
        chart.invalidate();

        return view;
    }

}
