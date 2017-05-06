package com.example.cristiana.weatherapp;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cristiana.weatherapp.model.Forecast;
import com.example.cristiana.weatherapp.model.Main;
import com.example.cristiana.weatherapp.model.Weather;
import com.example.cristiana.weatherapp.model.Wind;

import java.util.ArrayList;
import java.util.List;

public class DayDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateInfo();

    }

    public void updateInfo() {
        Forecast forecast = new Forecast();
        Main main =  new Main();
        Weather weather = new Weather();
        Wind wind = new Wind();
        List<Weather> weatherList = new ArrayList<>();

        main.setTemp(getIntent().getDoubleExtra("temperature", 0));
        main.setPressure(getIntent().getDoubleExtra("pressure", 0));
        weather.setDescription(getIntent().getStringExtra("description"));
        wind.setSpeed(getIntent().getDoubleExtra("wind_speed", 0));
        weatherList.add(weather);

        forecast.setMain(main);
        forecast.setWeather(weatherList);
        forecast.setWind(wind);

        Fragment details = DayDetailsFragment.New(forecast, getIntent().getStringExtra("location"));
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, details)
                .commit();
    }
}
