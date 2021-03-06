package com.example.singlediary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.Provider;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.android.volley.Request;
import com.example.singlediary.data.WeatherItem;
import com.example.singlediary.data.WeatherResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends AppCompatActivity
        implements OnTabItemSelectedListener, OnRequestListener,
                    MyApplication.OnResponseListener
{
    String TAG = "MainActivity";


    BottomNavigationView bottomNavigationView;
    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;

    int locationCount = 0;
    int PERMISSION_REQUEST_CODE;

    Date currentDate;
    String currentDateToString;
    Location currentLocation;
    GPSListener gpsListener;
    String currentWeather;
    String currentAddress;

    public static NoteDatabase mDatabase = null;

    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();


        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragment1).commit();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            Fragment selected;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.botTab_1:
                        selected = fragment1;
                        break;
                    case R.id.botTab_2:
                        selected = fragment2;
                        break;
                    case R.id.botTab_3:
                        selected = fragment3;
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selected).commit();
                return true;
            }
        });

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);

        openDatabase();
    }

    public void openDatabase(){
        if(mDatabase != null){
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = NoteDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
    }

    public void setPicturePath() {
        String folderPath = getFilesDir().getAbsolutePath();
        AppConstants.FOLDER_PHOTO = folderPath + File.separator + "photo";

        File photoFolder = new File(AppConstants.FOLDER_PHOTO);
        if (!photoFolder.exists()) {
            photoFolder.mkdirs();
        }
    }


    @Override
    public void onTabSelected(int position) {
        switch (position) {
            case 0:
                bottomNavigationView.setSelectedItemId(R.id.botTab_1);
                break;
            case 1:
                bottomNavigationView.setSelectedItemId(R.id.botTab_2);
                break;
            case 2:
                bottomNavigationView.setSelectedItemId(R.id.botTab_3);
                break;
        }
    }


    @Override
    public void onRequest(String command) {
        if (command != null) {
            if (command.equals("getCurrentLocation")) {
                getCurrentLocation();
            }
        }
    }


    public void getCurrentLocation() {
        currentDate = new Date();
        currentDateToString = AppConstants.dateFormat3.format(currentDate);
        if (fragment2 != null) {
            fragment2.setDateString(currentDateToString);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // ????????? ????????? ??????
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
                // ???????????? ?????? ????????? ?????? ??????

                    println("?????? ????????? ?????????????????????");
            } else {
                // ???????????? ?????? ????????? ????????? ????????? ???????????? ????????? ?????? ??????
                println("?????? ????????? ????????? ?????????????????????");

            }
            return;
        } else {
            // ????????? ??????
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation != null){
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                String message = "?????? ????????? -> latitude :" + latitude + ", longitude : " + longitude + " ?????????.";
                println(message);

                getCurrentWeather();
                getCurrentAddress();
            }

            gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
        }


    }

    public void stopLocationService(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.removeUpdates(gpsListener);
        println("Current location requested");
    }

    @Override
    public void processResponse(int requestCode, int responseCode, String response) {
        if(responseCode == 200) {
            if (requestCode == AppConstants.REQ_WEATHER_BY_GRID) {
                XmlParserCreator parserCreator = new XmlParserCreator() {
                    @Override
                    public XmlPullParser createParser() {
                        try {
                            return XmlPullParserFactory.newInstance().newPullParser();
                        } catch (XmlPullParserException e) {
                            throw new RuntimeException();
                        }
                    }
                };

                GsonXml gsonXml = new GsonXmlBuilder().setXmlParserCreator(parserCreator).setSameNameLists(true).create();

                WeatherResult weather = gsonXml.fromXml(response, WeatherResult.class);

                try {
                    Date tmDate = AppConstants.dateFormat.parse(weather.header.tm);
                    String tmDateText = AppConstants.dateFormat2.format(tmDate);
                    println("?????? ??????: " + tmDateText);

                    for (int i = 0; i < weather.body.datas.size(); i++) {
                        WeatherItem item = weather.body.datas.get(i);
                        println("#" + i + "?????? :" + item.hour + "???, " + item.day + "??????");
                        println("  ??????: " + item.wfKor);
                        println("  ??????: " + item.temp + " C");
                        println("  ????????????: " + item.pop + "%");

                        println("debug 1 : " + (int) Math.round(item.ws * 10));
                        float ws = Float.valueOf(String.valueOf((int) Math.round(item.ws * 10))) / 10.0f;
                        println("   ??????: " + ws + " m/s");
                    }

                    WeatherItem item = weather.body.datas.get(0);
                    currentWeather = item.wfKor;
                    if (fragment2 != null) {
                        fragment2.setWeather(item.wfKor);
                    }

                    if (locationCount > 0) {
                        stopLocationService();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                println("Unknown Request Code : " + requestCode);
            }
        } else {
            println("Failure response Code : " + responseCode);
        }
    }

    public class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            currentLocation = location;
            locationCount++;

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String message = "?????? ????????? -> latitude :" + latitude + ", longitude : " + longitude + " ?????????.";
            println(message);

            getCurrentWeather();
            getCurrentAddress();


        }
    }

    public void getCurrentWeather(){
        Map<String, Double> gridMap = GridUtil.getGrid(currentLocation.getLatitude(), currentLocation.getLongitude());
        double gridX = gridMap.get("x");
        double gridY = gridMap.get("y");
        println("x -> " + gridX + " y -> " + gridY);

        sendLocalWeatherRequest(gridX, gridY);
    }

    public void sendLocalWeatherRequest(double gridX, double gridY){
        String url = "http://www.kma.go.kr/wid/queryDFS.jsp";
        url += "?gridX=" + Math.round(gridX);
        url += "&gridY=" + Math.round(gridY);

        Map<String, String> params = new HashMap<String, String>();
        MyApplication.send(Request.Method.GET, url, AppConstants.REQ_WEATHER_BY_GRID, this, params);
    }

    public void getCurrentAddress(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(addresses != null && addresses.size() > 0){
            Address address = addresses.get(0);
            String addressLocality = address.getLocality() != null ? address.getLocality() : " ";
            currentAddress = addressLocality + " " + address.getSubLocality();
            String adminArea = address.getAdminArea();
            String country = address.getCountryName();
            println("Address : " + country + " " + adminArea + " " + currentAddress);

            if(fragment2 != null){
                fragment2.setAddress(currentAddress);
            }
        }

    }

    public void println(String data){
        Log.d(TAG, data);
    }
}