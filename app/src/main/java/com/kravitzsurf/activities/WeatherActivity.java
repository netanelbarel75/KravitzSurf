package com.kravitzsurf.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.kravitzsurf.R;
import com.kravitzsurf.models.WeatherData;
import com.kravitzsurf.services.WeatherApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {
    
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String WEATHER_API_KEY = "15c7876084828eb9704ac65e3977319f";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    // Free tier allows 60 calls/minute, 1,000 calls/day
    
    private TextView temperatureTextView, weatherDescTextView, waveHeightTextView;
    private TextView windSpeedTextView, humidityTextView, locationTextView;
    private ProgressBar progressBar;
    private FusedLocationProviderClient fusedLocationClient;
    private WeatherApiService weatherApiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        
        getSupportActionBar().setTitle("Weather & Wave Conditions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        initViews();
        setupRetrofit();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        checkLocationPermissionAndGetWeather();
    }
    
    private void initViews() {
        temperatureTextView = findViewById(R.id.temperatureTextView);
        weatherDescTextView = findViewById(R.id.weatherDescTextView);
        waveHeightTextView = findViewById(R.id.waveHeightTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        locationTextView = findViewById(R.id.locationTextView);
        progressBar = findViewById(R.id.progressBar);
    }
    
    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        weatherApiService = retrofit.create(WeatherApiService.class);
    }
    
    private void checkLocationPermissionAndGetWeather() {
        if (ActivityCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocationAndWeather();
        }
    }
    
    private void getLocationAndWeather() {
        progressBar.setVisibility(View.VISIBLE);
        
        if (ActivityCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        fetchWeatherData(location.getLatitude(), location.getLongitude());
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Cannot get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void fetchWeatherData(double latitude, double longitude) {
        weatherApiService.getWeatherData(latitude, longitude, WEATHER_API_KEY, "metric")
                .enqueue(new Callback<WeatherData>() {
                    @Override
                    public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                        progressBar.setVisibility(View.GONE);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            updateUI(response.body());
                        } else {
                            Toast.makeText(WeatherActivity.this, 
                                "Failed to get weather data", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<WeatherData> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(WeatherActivity.this, 
                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void updateUI(WeatherData weatherData) {
        temperatureTextView.setText(String.format("%.1f°C", weatherData.getMain().getTemp()));
        weatherDescTextView.setText(weatherData.getWeather().get(0).getDescription());
        windSpeedTextView.setText(String.format("Wind: %.1f m/s", weatherData.getWind().getSpeed()));
        humidityTextView.setText(String.format("Humidity: %d%%", weatherData.getMain().getHumidity()));
        locationTextView.setText(weatherData.getName());
        
        // Calculate approximate wave height based on wind speed
        double windSpeed = weatherData.getWind().getSpeed();
        double estimatedWaveHeight = calculateWaveHeight(windSpeed);
        waveHeightTextView.setText(String.format("Wave Height: %.1f m", estimatedWaveHeight));
    }
    
    private double calculateWaveHeight(double windSpeed) {
        // Simple estimation formula: wave height ≈ 0.2 * wind speed
        return windSpeed * 0.2;
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndWeather();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
