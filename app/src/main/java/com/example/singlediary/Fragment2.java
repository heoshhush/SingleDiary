package com.example.singlediary;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class Fragment2 extends Fragment {

    Context context;
    OnTabItemSelectedListener onTabItemSelectedListener;
    OnRequestListener onRequestListener;
    TextView frag2_date;
    ImageView weatherIcon;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
        if(context instanceof OnTabItemSelectedListener){
            onTabItemSelectedListener = (OnTabItemSelectedListener) context;
        }

        if(context instanceof OnRequestListener){
            onRequestListener = (OnRequestListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_2, container, false);
        initUI(rootView);

        onRequestListener.onRequest("getCurrentLocation");
        return rootView;
    }

    public void initUI(ViewGroup rootView){
        Button saveBtn = rootView.findViewById(R.id.frag2_btn1);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabItemSelectedListener.onTabSelected(0);
            }
        });

        Button deleteBtn = rootView.findViewById(R.id.frag2_btn2);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabItemSelectedListener.onTabSelected(0);
            }
        });

        Button cancelBtn = rootView.findViewById(R.id.frag2_btn3);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabItemSelectedListener.onTabSelected(0);
            }
        });

        SeekBar seekBar = rootView.findViewById(R.id.frag2_seekBar);
        seekBar.setProgress(2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("Mood", Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        frag2_date = rootView.findViewById(R.id.frag2_date);
        weatherIcon = rootView.findViewById(R.id.frag2_weatherIcon);

    }

    public void setWeather(String data) {
        if (data != null) {
            if (data.equals("맑음")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_1);
            } else if (data.equals("구름 조금")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_2);
            } else if (data.equals("구름 많음")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_3);
            } else if (data.equals("흐림")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_4);
            } else if (data.equals("비")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_5);
            } else if (data.equals("눈/비")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_6);
            } else if (data.equals("눈")) {
                weatherIcon.setImageResource(R.drawable.weather_icon_7);
            } else {
                Log.d("Fragment2", "Unknown weather string : " + data);
            }
        }
    }

    public void setDateString(String dateString){
        frag2_date.setText(dateString);
    }


}