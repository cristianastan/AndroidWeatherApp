package com.example.cristiana.weatherapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristiana.weatherapp.database.DbContract;
import com.example.cristiana.weatherapp.database.WeatherContentProvider;
import com.example.cristiana.weatherapp.model.Forecast;
import com.example.cristiana.weatherapp.model.Main;
import com.example.cristiana.weatherapp.model.TenDaysWeather;
import com.example.cristiana.weatherapp.model.Weather;
import com.example.cristiana.weatherapp.model.WeatherApp;
import com.example.cristiana.weatherapp.model.Wind;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Cristiana on 4/28/2017.
 */

public class TenDaysActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    Adapter adapter;
    boolean mLandscape;

    public static final String LOC_SAVED = "loc_saved";
    String mLocationName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendays);

        /* get the location name */
        mLocationName = getIntent().getStringExtra(LOC_SAVED);

        /* set layout manager */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /* check the orientation */
        mLandscape = (findViewById(R.id.container) != null);

        /* set adapter */
        adapter = new Adapter(new Adapter.Callback() {

            @Override
            public void show(Forecast forecast) {
                if(mLandscape) {
                    Fragment details = DayDetailsFragment.New(forecast, mLocationName);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, details)
                            .commit();
                } else {
                    Intent intent = new Intent(TenDaysActivity.this, DayDetailsActivity.class);
                    intent.putExtra("location", mLocationName);
                    intent.putExtra("date", forecast.getDtTxt());
                    intent.putExtra("temperature", forecast.getMain().getTemp());
                    intent.putExtra("description", forecast.getWeather().get(0).getDescription());
                    intent.putExtra("pressure", forecast.getMain().getPressure());
                    intent.putExtra("wind_speed", forecast.getWind().getSpeed());
                    startActivity(intent);
                }

            }
        });
        mRecyclerView.setAdapter(adapter);

        updateUIFromDB();

        fetchWeather();
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

    private void updateUIFromDB() {
        /* Prepare selection to take the current location */
        String selection = DbContract.Day.LOCATION + " = ?";
        String[] selectionArgs = { mLocationName };

        Cursor cursor = getContentResolver().query(WeatherContentProvider.DAY_URI, null, selection,
                        selectionArgs, null, null);

        if (cursor != null) {
            /* Move to the first position */
            if (cursor.moveToFirst()) {
                List<Forecast> forecasts = new ArrayList<>();

                /* get the column ids */
                int idIndex = cursor.getColumnIndex(DbContract.Day.ID);
                int locationIndex = cursor.getColumnIndex(DbContract.Day.LOCATION);
                int dateIndex = cursor.getColumnIndex(DbContract.Day.DATE);
                int temperatureIndex = cursor.getColumnIndex(DbContract.Day.TEMPERATURE);
                int descriptionIndex = cursor.getColumnIndex(DbContract.Day.DESCRIPTIOn);
                int pressureIndex = cursor.getColumnIndex(DbContract.Day.PRESSURE);
                int windSpeedIndex = cursor.getColumnIndex(DbContract.Day.WIND_SPEED);

                do {
                    Main main = new Main();
                    Weather weather = new Weather();
                    List<Weather> weatherList = new ArrayList<>();
                    Wind wind = new Wind();
                    Forecast forecast = new Forecast();

                    forecast.setDt(cursor.getInt(idIndex));
                    main.setTemp(cursor.getDouble(temperatureIndex));
                    main.setPressure(cursor.getDouble(pressureIndex));
                    weather.setDescription(cursor.getString(descriptionIndex));
                    wind.setSpeed(cursor.getDouble(windSpeedIndex));

                    weatherList.add(weather);
                    forecast.setDtTxt(cursor.getString(dateIndex));
                    forecast.setWeather(weatherList);
                    forecast.setMain(main);
                    forecast.setWind(wind);

                    mLocationName = cursor.getString(locationIndex);

                    forecasts.add(forecast);

                } while (cursor.moveToNext());

                adapter.setData(forecasts);
                adapter.notifyDataSetChanged();

                cursor.close();
            }
        }
    }

    private void handleNetworkResponse(TenDaysWeather tenDaysWeather) {
        List<Forecast> forecasts = tenDaysWeather.getForecast();

        for (Forecast forecast : forecasts) {
            ContentValues values = new ContentValues();

            /* Add the information to the content values */
            values.put(DbContract.Day.ID, forecast.getDt());
            values.put(DbContract.Day.LOCATION, mLocationName);
            values.put(DbContract.Day.DATE, forecast.getDtTxt());
            values.put(DbContract.Day.TEMPERATURE, forecast.getMain().getTemp());
            values.put(DbContract.Day.DESCRIPTIOn, forecast.getWeather().get(0).getDescription());
            values.put(DbContract.Day.PRESSURE, forecast.getMain().getPressure());
            values.put(DbContract.Day.WIND_SPEED, forecast.getWind().getSpeed());

            try {
                getContentResolver().insert(WeatherContentProvider.DAY_URI, values);

            } catch (SQLiteException ignored) {
                String selection = DbContract.Day.ID + " = " + forecast.getDt();
                getContentResolver().update(WeatherContentProvider.DAY_URI, values, selection, null);
            }
        }

        updateUIFromDB();
    }

    private void fetchWeather() {
        String key = "61ae4591669ba9f38b46d26e6ee808d5";
        String units = "metric";
        Integer numDays = 16;

        Call<TenDaysWeather> callable = WeatherApp.Service.Get().getTenDaysWeather(mLocationName, units, numDays, key);

        callable.enqueue(new Callback<TenDaysWeather>() {
            @Override
            public void onResponse(Call<TenDaysWeather> call, Response<TenDaysWeather> response) {
                if (response.isSuccessful()) {
                    TenDaysWeather tenDaysWeather = response.body();
                    handleNetworkResponse(tenDaysWeather);
                } else {
                    Toast.makeText(TenDaysActivity.this, "onResponse was unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TenDaysWeather> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(TenDaysActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<Forecast> mForecast = new ArrayList<>();
        Callback mCallback;

        public Adapter(Callback callback) {
            mCallback = callback;
        }

        public interface Callback {
            void show (Forecast forecast);
        }

        public void setData(List<Forecast> forecasts) {
            for (Forecast forecast : forecasts) {
                mForecast.add(forecast);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_day, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((ViewHolder) holder).bind(mForecast.get(position), new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCallback.show(mForecast.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mForecast.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mCrtDate;
            private final TextView mCrtTemperature;

            public ViewHolder(View itemView) {
                super(itemView);

                mCrtDate = (TextView) itemView.findViewById(R.id.crtDay);
                mCrtTemperature = (TextView) itemView.findViewById(R.id.crtTemperature);
            }

            public void bind(Forecast forecast, View.OnClickListener onClickListener) {
                String[] months = {"January", "February", "March", "April", "May", "June", "July",
                                    "August", "September", "October", "November", "December"};
                DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                Calendar calendar = Calendar.getInstance();

                try {
                    Date mDate = mDateFormat.parse(forecast.getDtTxt());
                    calendar.setTime(mDate);
                    mCrtDate.setText(months[calendar.get(Calendar.MONTH)] + " " +  calendar.get(Calendar.DAY_OF_MONTH)
                            + "\n" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mCrtTemperature.setText(forecast.getMain().getTemp().toString() + " C");
                itemView.setOnClickListener(onClickListener);
            }
        }
    }
}
