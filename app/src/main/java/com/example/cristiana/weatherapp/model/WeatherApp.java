package com.example.cristiana.weatherapp.model;

import com.example.cristiana.weatherapp.OneDayActivity;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Cristiana on 4/18/2017.
 */

public interface WeatherApp {
    @GET("/data/2.5/weather")
    Call<OneDayWeather> getOneDayWeather(@Query("q") String location,
                                         @Query("units") String units,
                                         @Query("appid") String key);

    @GET("/data/2.5/forecast")
    Call<TenDaysWeather> getTenDaysWeather(@Query("q") String location,
                                           @Query("units") String units,
                                           @Query("cnt") Integer numDays,
                                           @Query("appid") String key);

    class Service {
        private static WeatherApp sInstance;

        public synchronized static WeatherApp Get() {
            if (sInstance == null)
                sInstance = new Retrofit.Builder()
                        .baseUrl("http://api.openweathermap.org")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(WeatherApp.class);

            return sInstance;
        }
    }
}
