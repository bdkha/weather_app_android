package com.kha.myweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.kha.myweather.WeatherRVAdapter;
import com.kha.myweather.WeatherRVModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRl;
    private ProgressBar loadingPB;
    private TextView citynameTV,temperatureTV,conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV,iconIV,searchIV;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager ;
    private int PERMISSION_CODE = 1;
    private String cityName;
    private LocationListener locationListener;
    DecimalFormat df = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRl = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        citynameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this,weatherRVModelArrayList);
        weatherRV.setAdapter(weatherRVAdapter);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            cityName = "Vinh";
            getWeatherInfo(cityName);
        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdt.getText().toString().trim();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this,"Please Enter City Name",Toast.LENGTH_SHORT).show();

                }else {
                    citynameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName (double longitude, double latitude){
        String cityName ="Vinh";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
            else {
                Log.d("TAG","CITY_NOT_FOUND");
                Toast.makeText(this,"USER CITY NOT FOUND...",Toast.LENGTH_SHORT).show();
            }


        } catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {
        String baseUrl = "http://api.openweathermap.org/data/2.5/";
        String method = "weather?";
        String key = "48887d7f6b72eb3be5c5fd8a8ed4ae1c";
        String url = baseUrl+method+"q="+cityName+"&appid="+key;
        citynameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null , new Response.Listener<JSONObject>() {


            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRl.setVisibility(View.VISIBLE);

                try {
                    JSONArray weatherArray = response.getJSONArray("weather");
                    Double temp = response.getJSONObject("main").getDouble("temp") - 273.15;
                    temperatureTV.setText(df.format(temp)+"°C");
                    String condition = weatherArray.getJSONObject(0).getString("main");
                    conditionTV.setText(condition);
                    geticon(condition);
                    Double time = response.getDouble("dt");
                    Double sunset = response.getJSONObject("sys").getDouble("sunset");
                    Double sunrise = response.getJSONObject("sys").getDouble("sunrise");
                    if (time> sunrise && time < sunset) {
                        backIV.setImageResource(R.drawable.day);
                    } else{
                        backIV.setImageResource(R.drawable.night);
                    }
                    Double lon,lat;
                    lon = response.getJSONObject("coord").getDouble("lon");
                    lat = response.getJSONObject("coord").getDouble("lat");
                    getForecasts(lon,lat);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley",error.toString());
                Toast.makeText(MainActivity.this, "Please enter a valid city name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
    private  void getForecasts(double longitude, double latitude) {
        String key = "48887d7f6b72eb3be5c5fd8a8ed4ae1c";
        String endUrl = "https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&exclude=hourly,minutely&appid="+key;
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        weatherRVModelArrayList.clear();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, endUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray dailyArray = response.getJSONArray("daily");

                    for (int i=0;i<dailyArray.length();i++){
                        JSONObject dailyObj = dailyArray.getJSONObject(i);
                        String time =dailyObj.getString("dt");
                        String temper =dailyObj.getJSONObject("temp").getString("day");
                        String img =dailyObj.getJSONArray("weather").getJSONObject(0).getString("main");
                        String wind =dailyObj.getString("wind_speed");
                        weatherRVModelArrayList.add(new WeatherRVModel(time,temper,wind,img));
                    }
                    weatherRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley",error.toString());
            }
        }

        );

        requestQueue.add(jsonObjectRequest);
    }
    private void geticon(String condition) {
        switch (condition ) {
            case "Sun" :
               iconIV.setImageResource(R.drawable.sun);
               break;
            case  "Rain" :
                iconIV.setImageResource(R.drawable.rain);
                break;
            case "Clouds":
                iconIV.setImageResource(R.drawable.clouds);
                break;
            case "Snow" :
                iconIV.setImageResource(R.drawable.snow);
                break;
            default:
                iconIV.setImageResource(R.drawable.md);
        }
    }
}