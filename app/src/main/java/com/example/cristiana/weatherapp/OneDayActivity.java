package com.example.cristiana.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiana.weatherapp.model.OneDayWeather;
import com.example.cristiana.weatherapp.model.WeatherApp;

import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Cristiana on 4/18/2017.
 */

public class OneDayActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String LOC_SAVED = "loc_saved";
    String mLocationName;

    TextView mLocationValue;
    TextView mTemperatureValue;
    TextView mDescriptionValue;
    TextView mHumidityValue;
    TextView mPressureValue;
    Button mDaysButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oneday);

        /* get IDs of the elements from layout */
        getParameters();
        /* get the data from web */
        updateInfo();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.daysButton) {
            Intent intent = new Intent(this, TenDaysActivity.class);
            intent.putExtra(LOC_SAVED, mLocationName);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.oneday_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Return) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().remove(LOC_SAVED);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return false;
    }

    private void getParameters() {
        mLocationName = getIntent().getStringExtra(LOC_SAVED);
        mLocationValue = (TextView) findViewById(R.id.locationValue);
        mTemperatureValue = (TextView) findViewById(R.id.temperatureValue);
        mDescriptionValue = (TextView) findViewById(R.id.descriptionValue);
        mHumidityValue = (TextView) findViewById(R.id.humidityValue);
        mPressureValue = (TextView) findViewById(R.id.pressureValue);
        mDaysButton = (Button) findViewById(R.id.daysButton);

        String locName = mLocationName.substring(0, 1).toUpperCase() + mLocationName.substring(1);
        mLocationName = locName;

        mDaysButton.setOnClickListener(this);
    }

    private String errorMessage(int code) {
        String message = "An error occured";
        switch (code) {
            case 401: message = "Invalid location"; break;
            case 404: message = "Page not found"; break;
            case 500: message = "Internal server error"; break;
            case 503: message = "Serice unavailable"; break;
            case 550: message = "Permission denied"; break;
        }

        return message;
    }

    private String failureMessage(Throwable t) {
        String message = "Connection error";

        if (t instanceof UnknownHostException)
            message = "No Internet connecion found";

        if (t instanceof TimeoutException)
            message = "Connection time expired";

        return message;
    }

    private void updateInfo() {
        /* show the searched location */
        String key = "61ae4591669ba9f38b46d26e6ee808d5";
        String units = "metric";

        mLocationValue.setText(mLocationName);

        /* make HTTP request for the rest of the information */
        Call<OneDayWeather> callable = WeatherApp.Service.Get().getOneDayWeather(mLocationName, units, key);

        callable.enqueue(new Callback<OneDayWeather>() {

            @Override
            public void onResponse(Call<OneDayWeather> call, Response<OneDayWeather> response) {
                if (response.isSuccessful()) {
                    OneDayWeather weatherInfo = response.body();
                    mTemperatureValue.setText(weatherInfo.getMain().getTemp().toString() + " C");
                    mDescriptionValue.setText(weatherInfo.getWeather().get(0).getDescription());
                    mHumidityValue.setText(weatherInfo.getMain().getHumidity().toString());
                    mPressureValue.setText(weatherInfo.getMain().getPressure().toString());
                } else {
                    String message = errorMessage(response.code());
                    Toast.makeText(OneDayActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OneDayWeather> call, Throwable t) {
                t.printStackTrace();
                String message = failureMessage(t);
                Toast.makeText(OneDayActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
