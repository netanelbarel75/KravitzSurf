package com.kravitzsurf.services;

import com.kravitzsurf.models.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    
    @GET("weather")
    Call<WeatherData> getWeatherData(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
}
